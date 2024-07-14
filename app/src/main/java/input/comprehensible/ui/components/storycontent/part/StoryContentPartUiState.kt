package input.comprehensible.ui.components.storycontent.part

import android.graphics.Bitmap
import input.comprehensible.data.stories.model.StoryElement

/**
 * Represents the UI state of a story content part.
 */
sealed interface StoryContentPartUiState {
    /**
     * Represents a paragraph content part.
     */
    data class Paragraph(val paragraph: String) : StoryContentPartUiState

    /**
     * Represents an image content part.
     */
    data class Image(
        val contentDescription: String,
        val bitmap: Bitmap
    ) : StoryContentPartUiState
}


/**
 * Converts a [StoryElement] to a [StoryContentPartUiState].
 */
fun StoryElement.toStoryContentPartUiState(areTranslationsEnabled: Boolean) = when (this) {
    is StoryElement.Paragraph -> StoryContentPartUiState.Paragraph(
        paragraph = if (areTranslationsEnabled) translation else text
    )
    is StoryElement.Image -> StoryContentPartUiState.Image(
        contentDescription = contentDescription,
        bitmap = bitmap
    )
}
