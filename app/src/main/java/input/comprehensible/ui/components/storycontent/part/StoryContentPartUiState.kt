package input.comprehensible.ui.components.storycontent.part

import android.graphics.Bitmap
/**
 * Represents the UI state of a story content part.
 */
sealed interface StoryContentPartUiState {
    /**
     * Represents a paragraph content part.
     */
    data class Paragraph(
        val paragraphIndex: Int,
        val sentences: List<String>,
        val translatedSentences: List<String>,
        val onClick: (sentenceIndex: Int) -> Unit,
    ) : StoryContentPartUiState

    /**
     * Represents an image content part.
     */
    data class Image(
        val contentDescription: String,
        val bitmap: Bitmap
    ) : StoryContentPartUiState

    /**
     * Represents a set of choices available to the reader.
     */
    data class Choices(
        val options: List<Option>,
    ) : StoryContentPartUiState {
        data class Option(
            val text: String,
            val onClick: () -> Unit,
        )
    }

    /**
     * Represents a previously selected choice.
     */
    data class ChosenChoice(val text: String) : StoryContentPartUiState
}
