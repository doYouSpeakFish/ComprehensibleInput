package input.storygen

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class StoryDocument(
    val id: String,
    val title: String,
    val startPartId: String,
    val featuredImagePath: String = "",
    val parts: List<StoryPart>,
)

@Serializable
data class StoryPart(
    val id: String,
    val content: List<StoryContent>,
    val choice: StoryChoice? = null,
)

@Serializable
data class StoryChoice(
    val text: String,
    val parentPartId: String,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("type")
sealed interface StoryContent

@Serializable
@SerialName("paragraph")
data class StoryParagraph(
    val sentences: List<String>,
) : StoryContent

@Serializable
@SerialName("image")
data class StoryImage(
    val path: String,
    val contentDescription: String,
) : StoryContent
