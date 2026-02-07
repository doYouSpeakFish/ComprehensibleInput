package input.comprehensible.ui.textadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.data.textadventures.TextAdventureResult
import input.comprehensible.data.textadventures.model.TextAdventureMessage
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender
import input.comprehensible.data.textadventures.model.TextAdventureParagraph
import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState
import input.comprehensible.usecases.ContinueTextAdventureUseCase
import input.comprehensible.usecases.GetTextAdventureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TextAdventureViewModel(
    private val adventureId: String,
    getTextAdventureUseCase: GetTextAdventureUseCase = GetTextAdventureUseCase(),
    private val continueTextAdventureUseCase: ContinueTextAdventureUseCase = ContinueTextAdventureUseCase(),
) : ViewModel() {
    private val adventureFlow = getTextAdventureUseCase(adventureId)
    private val selectedText = MutableStateFlow<TextAdventureUiState.SelectedText?>(null)
    private val inputText = MutableStateFlow("")

    val state = combine(
        adventureFlow,
        selectedText,
        inputText,
    ) { adventureResult, selectedSentence, inputTextValue ->
        when (adventureResult) {
            TextAdventureResult.Error -> TextAdventureUiState.Error
            is TextAdventureResult.Success -> TextAdventureUiState.Loaded(
                title = adventureResult.adventure.title,
                messages = adventureResult.adventure.messages.map { it.toUiState() },
                selectedText = selectedSentence,
                inputText = inputTextValue,
                isInputEnabled = !adventureResult.adventure.isComplete,
            )
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = TextAdventureUiState.Loading,
    )

    fun onInputChanged(value: String) {
        inputText.value = value
    }

    fun onSendMessage() {
        val message = inputText.value.trim()
        if (message.isBlank()) return
        viewModelScope.launch {
            continueTextAdventureUseCase(adventureId = adventureId, userMessage = message)
            inputText.value = ""
        }
    }

    fun onSentenceSelected(messageId: String, paragraphIndex: Int, sentenceIndex: Int) {
        selectedText.update { selectedSentence ->
            if (selectedSentence?.messageId == messageId &&
                selectedSentence.paragraphIndex == paragraphIndex &&
                selectedSentence.sentenceIndex == sentenceIndex
            ) {
                return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
            }
            TextAdventureUiState.SelectedText(
                messageId = messageId,
                paragraphIndex = paragraphIndex,
                sentenceIndex = sentenceIndex,
                isTranslated = true,
            )
        }
    }
}

private fun TextAdventureMessage.toUiState(): TextAdventureMessageUiState = when (sender) {
    TextAdventureMessageSender.AI -> TextAdventureMessageUiState.Ai(
        id = id,
        paragraphs = paragraphs.map { it.toUiState() },
        isEnding = isEnding,
    )
    TextAdventureMessageSender.USER -> TextAdventureMessageUiState.User(
        id = id,
        text = paragraphs.flatMap { it.sentences }.joinToString(" "),
    )
}

private fun TextAdventureParagraph.toUiState() = StoryContentPartUiState.Paragraph(
    sentences = sentences,
    translatedSentences = translatedSentences,
)
