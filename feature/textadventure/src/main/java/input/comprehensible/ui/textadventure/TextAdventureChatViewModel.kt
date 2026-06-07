package input.comprehensible.ui.textadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.data.account.AccountRepository
import input.comprehensible.data.account.sources.local.Session
import input.comprehensible.data.languagesettings.LanguageSettingsRepository
import input.comprehensible.data.textadventure.AdventureMessage
import input.comprehensible.data.textadventure.AdventureMessageSender
import input.comprehensible.data.textadventure.TextAdventureRepository
import input.comprehensible.data.textadventure.sources.remote.isRateLimited
import input.comprehensible.ui.textadventure.TextAdventureChatUiState.SelectedSentence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the chat screen. A new adventure ([adventureId] is null) is started immediately, showing a
 * generating placeholder until the first AI message arrives or an error and retry appear. An
 * existing adventure shows its cached messages at once while it refreshes from the backend.
 *
 * The user continues the conversation through the input bar: their message is shown optimistically
 * at once, submitted ([TextAdventureRepository.sendUserMessage]) so it becomes tap-to-translate, and
 * then the AI reply is generated behind the cycling placeholder. A failed submit or generation shows
 * an error with a retry that resumes from where it failed. AI (and submitted user) sentences can be
 * tapped to toggle their translation, mirroring the story reader.
 */
class TextAdventureChatViewModel(
    private val adventureId: String?,
    private val accountRepository: AccountRepository = AccountRepository(),
    private val textAdventureRepository: TextAdventureRepository = TextAdventureRepository(),
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
) : ViewModel() {

    private val currentAdventureId = MutableStateFlow(adventureId)
    private val isGenerating = MutableStateFlow(false)
    private val showError = MutableStateFlow(false)
    private val showBusyMessage = MutableStateFlow(false)
    private val showMessageError = MutableStateFlow(false)
    private val optimisticUserMessage = MutableStateFlow<ChatMessage?>(null)
    private val selectedSentence = MutableStateFlow<SelectedSentence?>(null)
    private var pendingRetry: (() -> Unit)? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val messages = currentAdventureId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else textAdventureRepository.getMessages(id)
    }.map { messages -> messages.map { it.toChatMessage() } }

    val state: StateFlow<TextAdventureChatUiState> = combine(
        messages,
        isGenerating,
        showError,
        showBusyMessage,
        combine(showMessageError, optimisticUserMessage, selectedSentence, ::Triple),
    ) { messages, generating, error, busy, (messageError, optimistic, selected) ->
        TextAdventureChatUiState(
            messages = messages,
            isGenerating = generating,
            showError = error,
            showBusyMessage = busy,
            showMessageError = messageError,
            optimisticUserMessage = optimistic,
            selectedSentence = selected,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = TextAdventureChatUiState.INITIAL,
    )

    init {
        if (adventureId == null) startAdventure() else refreshMessages(adventureId)
    }

    fun onRetry() {
        pendingRetry?.invoke()
    }

    /** Sends a user message: shown optimistically, then submitted and answered by the AI. */
    fun onSendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        optimisticUserMessage.value = optimisticMessage(trimmed)
        submitUserMessage(trimmed)
    }

    /** Toggles the translation of a tapped sentence, or selects a newly tapped one. */
    fun onSentenceSelected(messageId: String, paragraphIndex: Int, sentenceIndex: Int) {
        selectedSentence.update { current ->
            val same = current != null &&
                current.messageId == messageId &&
                current.paragraphIndex == paragraphIndex &&
                current.sentenceIndex == sentenceIndex
            if (same) {
                current.copy(isTranslated = !current.isTranslated)
            } else {
                SelectedSentence(messageId, paragraphIndex, sentenceIndex, isTranslated = true)
            }
        }
    }

    private fun startAdventure() {
        pendingRetry = ::startAdventure
        viewModelScope.launch {
            clearGenerationErrors()
            isGenerating.value = true
            val session = currentSession()
            val learningLanguage = languageSettingsRepository.learningLanguage.first()
            val translationLanguage = languageSettingsRepository.translationsLanguage.first()
            textAdventureRepository
                .startAdventure(session.token, session.userId, learningLanguage, translationLanguage)
                .onSuccess { newAdventureId ->
                    currentAdventureId.value = newAdventureId
                    isGenerating.value = false
                }
                .onFailure { showGenerationFailure(it) }
        }
    }

    private fun submitUserMessage(text: String) {
        pendingRetry = { submitUserMessage(text) }
        viewModelScope.launch {
            showMessageError.value = false
            showBusyMessage.value = false
            val adventureId = currentAdventureId.value ?: return@launch
            val parentId = messages.first().lastOrNull()?.id ?: return@launch
            textAdventureRepository
                .sendUserMessage(currentSession().token, adventureId, parentId, text)
                .onSuccess { userMessage ->
                    optimisticUserMessage.value = null
                    generateAiMessage(adventureId, userMessage.id)
                }
                .onFailure {
                    if (it.isRateLimited()) showBusyMessage.value = true else showMessageError.value = true
                }
        }
    }

    private fun generateAiMessage(adventureId: String, parentId: String) {
        pendingRetry = { generateAiMessage(adventureId, parentId) }
        viewModelScope.launch {
            clearGenerationErrors()
            isGenerating.value = true
            textAdventureRepository
                .generateAiMessage(currentSession().token, adventureId, parentId)
                .onSuccess { isGenerating.value = false }
                .onFailure { showGenerationFailure(it) }
        }
    }

    private fun clearGenerationErrors() {
        showError.value = false
        showBusyMessage.value = false
    }

    /**
     * A rate limit (HTTP 429) surfaces a "system busy" message; any other failure surfaces the
     * generic generation error. Both replace the generating placeholder and offer a retry.
     */
    private fun showGenerationFailure(throwable: Throwable) {
        isGenerating.value = false
        if (throwable.isRateLimited()) showBusyMessage.value = true else showError.value = true
    }

    private fun refreshMessages(id: String) {
        viewModelScope.launch {
            textAdventureRepository.refreshMessages(currentSession().token, id)
        }
    }

    private suspend fun currentSession(): Session = accountRepository.session.filterNotNull().first()

    private fun optimisticMessage(text: String) = ChatMessage(
        id = OPTIMISTIC_USER_MESSAGE_ID,
        sender = ChatMessageSender.USER,
        isEnding = false,
        paragraphs = listOf(
            // The translation is the text itself until the real one arrives, so tapping the
            // optimistic message to translate it leaves the same text visible rather than blank.
            ChatMessage.Paragraph(sentences = listOf(text), translatedSentences = listOf(text)),
        ),
    )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val OPTIMISTIC_USER_MESSAGE_ID = "optimistic-user-message"
    }
}

private fun AdventureMessage.toChatMessage() = ChatMessage(
    id = id,
    sender = when (sender) {
        AdventureMessageSender.AI -> ChatMessageSender.AI
        AdventureMessageSender.USER -> ChatMessageSender.USER
    },
    isEnding = isEnding,
    paragraphs = paragraphs.map { paragraph ->
        ChatMessage.Paragraph(
            sentences = paragraph.sentences,
            translatedSentences = paragraph.translatedSentences,
        )
    },
)
