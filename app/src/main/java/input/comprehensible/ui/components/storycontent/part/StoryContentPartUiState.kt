package input.comprehensible.ui.components.storycontent.part

import android.graphics.Bitmap
import androidx.compose.ui.text.TextRange

/**
 * Represents the UI state of a story content part.
 */
sealed interface StoryContentPartUiState {
    /**
     * Represents a paragraph content part.
     */
    data class Paragraph(
        val paragraph: String,
        val onClick: (characterIndex: Int) -> Unit,
        val selectedTextRange: TextRange?
    ) : StoryContentPartUiState

    /**
     * Represents an image content part.
     */
    data class Image(
        val contentDescription: String,
        val bitmap: Bitmap
    ) : StoryContentPartUiState
}
