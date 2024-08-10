package input.comprehensible.data.stories.sources.stories.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A story.
 */
@Serializable
data class StoryData(
    val id: String = "",
    val title: String,
    val content: List<StoryElementData>,
)

/**
 * An element of a story.
 */
@Serializable
sealed interface StoryElementData {
    /**
     * A paragraph of text.
     */
    @Serializable
    @SerialName("paragraph")
    data class ParagraphData(val sentences: List<String>) : StoryElementData

    /**
     * An image, with [contentDescription] and [path] to the image file.
     */
    @Serializable
    @SerialName("image")
    data class ImageData(
        val contentDescription: String,
        val path: String,
    ) : StoryElementData
}
