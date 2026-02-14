package input.comprehensible.ui.textadventure

import input.comprehensible.ui.components.storycontent.part.StoryContentPartUiState

sealed interface TextAdventureUiState {
    object Loading : TextAdventureUiState
    object Error : TextAdventureUiState

    data class Loaded(
        val title: String,
        val messages: List<TextAdventureMessageUiState>,
        val selectedText: SelectedText?,
        val inputText: String,
        val isInputEnabled: Boolean,
    ) : TextAdventureUiState

    data class SelectedText(
        val messageId: String,
        val paragraphIndex: Int,
        val sentenceIndex: Int,
        val isTranslated: Boolean,
    )
}

sealed interface TextAdventureMessageUiState {
    val id: String

    data class Ai(
        override val id: String,
        val paragraphs: List<StoryContentPartUiState.Paragraph>,
        val isEnding: Boolean,
    ) : TextAdventureMessageUiState

    data class User(
        override val id: String,
        val text: String,
    ) : TextAdventureMessageUiState
}
