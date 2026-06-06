package input.comprehensible.ui.textadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.data.account.AccountRepository
import input.comprehensible.data.languagesettings.LanguageSettingsRepository
import input.comprehensible.data.textadventure.TextAdventureRepository
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the chat screen. A new adventure ([adventureId] is null) is started immediately, showing a
 * generating placeholder until the first AI message arrives or an error and retry appear. An
 * existing adventure shows its cached messages at once while it refreshes from the backend. AI
 * sentences can be tapped to toggle their translation, mirroring the story reader.
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
    private val selectedSentence = MutableStateFlow<SelectedSentence?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val messages = currentAdventureId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else textAdventureRepository.getMessages(id)
    }

    val state: StateFlow<TextAdventureChatUiState> = combine(
        messages,
        isGenerating,
        showError,
        selectedSentence,
    ) { messages, generating, error, selected ->
        TextAdventureChatUiState(
            messages = messages,
            isGenerating = generating,
            showError = error,
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
        startAdventure()
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
        viewModelScope.launch {
            showError.value = false
            isGenerating.value = true
            val session = accountRepository.session.filterNotNull().first()
            val learningLanguage = languageSettingsRepository.learningLanguage.first()
            val translationLanguage = languageSettingsRepository.translationsLanguage.first()
            textAdventureRepository
                .startAdventure(session.token, session.userId, learningLanguage, translationLanguage)
                .onSuccess { newAdventureId ->
                    currentAdventureId.value = newAdventureId
                    isGenerating.value = false
                }
                .onFailure {
                    isGenerating.value = false
                    showError.value = true
                }
        }
    }

    private fun refreshMessages(id: String) {
        viewModelScope.launch {
            val session = accountRepository.session.filterNotNull().first()
            textAdventureRepository.refreshMessages(session.token, id)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
