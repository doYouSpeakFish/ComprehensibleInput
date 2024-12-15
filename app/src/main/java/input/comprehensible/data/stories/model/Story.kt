package input.comprehensible.data.stories.model

import android.graphics.Bitmap

/**
 * A story that can be read.
 */
data class Story(
    val id: String,
    val title: String,
    val translatedTitle: String,
    val content: List<StoryElement>,
    val currentStoryElementIndex: Int,
)

/**
 * A part of a story that can be read.
 */
sealed interface StoryElement {
    /**
     * A paragraph of text.
     */
    data class Paragraph(
        val sentences: List<String>,
        val sentencesTranslations: List<String>,
    ) : StoryElement

    /**
     * An image in a story.
     */
    data class Image(
        val contentDescription: String,
        val bitmap: Bitmap
    ) : StoryElement
}
