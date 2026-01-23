package input.comprehensible.ui.textadventure

import input.comprehensible.data.textadventure.model.TextAdventureRole

sealed interface TextAdventureUiState {
    data object Loading : TextAdventureUiState

    data class Loaded(
        val messages: List<TextAdventureMessageUiState>,
        val selectedSentence: SelectedSentence?,
        val isInputEnabled: Boolean,
    ) : TextAdventureUiState

    data class SelectedSentence(
        val messageId: String,
        val sentenceIndex: Int,
        val isTranslated: Boolean,
    )
}

data class TextAdventureMessageUiState(
    val id: String,
    val role: TextAdventureRole,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
) {
    val displayText: String
        get() = sentences.joinToString(" ")

    val isFromAi: Boolean
        get() = role == TextAdventureRole.AI
}
