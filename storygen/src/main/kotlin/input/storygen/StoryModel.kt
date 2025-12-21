package input.storygen

import kotlinx.serialization.Serializable

interface StoryPartsDataSource {
    suspend fun generatePart(request: StoryModelRequest): StoryModelResult
}

data class StoryModelRequest(
    val storyId: String,
    val title: String,
    val language: StoryLanguage,
    val targetPartId: String,
    val parentChoice: StoryBranch?,
    val currentDepth: Int,
    val maxDepth: Int,
    val stepsRemaining: Int,
)

@Serializable
data class StoryModelPart(
    val id: String,
    val content: List<StoryContent>,
    val featuredImagePath: String? = null,
)

@Serializable
data class StoryModelChoice(
    val nextPartId: String,
    val text: String,
)

@Serializable
data class StoryModelResult(
    val part: StoryModelPart,
    val choices: List<StoryModelChoice>,
)

data class StoryBranch(
    val parentPartId: String,
    val choiceText: String,
)
