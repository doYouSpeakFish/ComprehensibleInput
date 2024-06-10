package input.comprehensible.ui.components.storycontent.part

import input.comprehensible.data.stories.model.StoryElement

/**
 * Represents the UI state of a story content part.
 */
sealed interface StoryContentPartUiState {
    data class Paragraph(val paragraph: String) : StoryContentPartUiState
}


/**
 * Converts a [StoryElement] to a [StoryContentPartUiState].
 */
fun StoryElement.toStoryContentPartUiState() = when (this) {
    is StoryElement.Paragraph -> StoryContentPartUiState.Paragraph(text)
}
