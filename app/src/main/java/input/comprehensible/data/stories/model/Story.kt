package input.comprehensible.data.stories.model

import android.graphics.Bitmap

/**
 * A story that can be read.
 */
data class Story(
    val id: String,
    val title: String,
    val translatedTitle: String,
    val parts: List<StoryPart>,
    val currentPartId: String,
    val storyPosition: Int,
)

data class StoryPart(
    val id: String,
    val leadingChoice: StoryChoiceOption?,
    val elements: List<StoryElement>,
    val choice: StoryChoice?,
)

sealed interface StoryChoice {
    data class Available(val options: List<StoryChoiceOption>) : StoryChoice
    data class Chosen(val option: StoryChoiceOption) : StoryChoice
}

data class StoryChoiceOption(
    val text: String,
    val translatedText: String,
    val targetPartId: String,
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
