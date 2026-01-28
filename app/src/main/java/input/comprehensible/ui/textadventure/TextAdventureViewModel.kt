package input.comprehensible.ui.textadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.textadventure.TextAdventureRepository
import input.comprehensible.ui.textadventure.TextAdventureUiState.Loaded
import input.comprehensible.ui.textadventure.TextAdventureUiState.SelectedSentence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TextAdventureViewModel(
    private val repository: TextAdventureRepository = TextAdventureRepository(),
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
    private val adventureId: String = DEFAULT_ADVENTURE_ID,
) : ViewModel() {
    private val selectedSentenceState = MutableStateFlow<SelectedSentence?>(null)
    private val languages = combine(
        languageSettingsRepository.learningLanguage,
        languageSettingsRepository.translationsLanguage,
    ) { learningLanguage, translationLanguage ->
        learningLanguage to translationLanguage
    }

    val state = combine(
        repository.adventure(adventureId),
        selectedSentenceState,
    ) { adventure, selectedSentence ->
        if (adventure == null) {
            TextAdventureUiState.Loading
        } else {
            Loaded(
                messages = adventure.messages.map { message ->
                    TextAdventureMessageUiState(
                        id = message.id,
                        role = message.role,
                        sentences = message.sentences,
                        translatedSentences = message.translatedSentences,
                        isEnding = message.isEnding,
                    )
                },
                selectedSentence = selectedSentence,
                isInputEnabled = !adventure.isComplete,
            )
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TextAdventureUiState.Loading,
    )

    init {
        viewModelScope.launch {
            val (learningLanguage, translationLanguage) = languages.first()
            repository.ensureAdventureStarted(
                adventureId = adventureId,
                learningLanguage = learningLanguage,
                translationLanguage = translationLanguage,
            )
        }
    }

    fun onSentenceSelected(
        messageId: String,
        sentenceIndex: Int,
    ) {
        selectedSentenceState.update { selectedSentence ->
            val isSameMessage = selectedSentence?.messageId == messageId
            val isSameSentence = selectedSentence?.sentenceIndex == sentenceIndex
            if (isSameMessage && isSameSentence) {
                return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
            }
            SelectedSentence(
                messageId = messageId,
                sentenceIndex = sentenceIndex,
                isTranslated = true,
            )
        }
    }

    fun onUserResponseSubmitted(response: String) {
        viewModelScope.launch {
            val (learningLanguage, translationLanguage) = languages.first()
            repository.submitResponse(
                adventureId = adventureId,
                learningLanguage = learningLanguage,
                translationLanguage = translationLanguage,
                userResponse = response,
            )
        }
    }

    companion object {
        const val DEFAULT_ADVENTURE_ID = "text-adventure"
    }
}
