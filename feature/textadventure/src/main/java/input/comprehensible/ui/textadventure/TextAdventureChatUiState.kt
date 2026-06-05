package input.comprehensible.ui.textadventure

import input.comprehensible.data.textadventure.AdventureMessage

/**
 * The UI state for the text adventure chat screen.
 */
data class TextAdventureChatUiState(
    val messages: List<AdventureMessage>,
    val isGenerating: Boolean,
    val showError: Boolean,
    val selectedSentence: SelectedSentence?,
) {
    /**
     * The sentence the user has tapped to translate, identified within the conversation, and
     * whether it is currently showing its translation.
     */
    data class SelectedSentence(
        val messageId: String,
        val paragraphIndex: Int,
        val sentenceIndex: Int,
        val isTranslated: Boolean,
    )

    companion object {
        val INITIAL = TextAdventureChatUiState(
            messages = emptyList(),
            isGenerating = false,
            showError = false,
            selectedSentence = null,
        )
    }
}
