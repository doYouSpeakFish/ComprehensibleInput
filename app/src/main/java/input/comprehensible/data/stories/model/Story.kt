package input.comprehensible.data.stories.model

/**
 * A story that can be read.
 */
data class Story(
    val id: String,
    val title: String,
    val content: List<StoryElement>,
)

/**
 * A part of a story that can be read.
 */
sealed interface StoryElement {
    /**
     * A paragraph of text.
     */
    data class Paragraph(val text: String) : StoryElement
}
