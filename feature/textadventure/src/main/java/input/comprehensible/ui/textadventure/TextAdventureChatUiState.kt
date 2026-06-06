package input.comprehensible.ui.textadventure

import input.comprehensible.data.textadventure.AdventureMessage

/**
 * The UI state for the text adventure chat screen.
 */
data class TextAdventureChatUiState(
    val messages: List<AdventureMessage>,
    val isGenerating: Boolean,
    val showError: Boolean,
    val showMessageError: Boolean,
    val optimisticUserMessage: AdventureMessage?,
    val selectedSentence: SelectedSentence?,
) {
    /** The persisted messages followed by the not-yet-submitted user message, if any. */
    val displayedMessages: List<AdventureMessage>
        get() = optimisticUserMessage?.let { messages + it } ?: messages

    /** The input is hidden once the conversation has reached an ending message. */
    val isInputHidden: Boolean
        get() = messages.lastOrNull()?.isEnding == true

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
            showMessageError = false,
            optimisticUserMessage = null,
            selectedSentence = null,
        )
    }
}
