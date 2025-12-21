package input.storygen

import java.nio.file.Path

data class StoryLanguage(
    val code: String,
    val label: String,
)

data class StoryPlan(
    val storyId: String,
    val title: String,
    val language: StoryLanguage,
    val maxDepth: Int,
    val startPartId: String = "start",
    val featuredImagePath: String? = null,
) {
    init {
        require(maxDepth > 0) { "maxDepth must be at least 1" }
        require(storyId.isNotBlank()) { "storyId cannot be blank" }
        require(title.isNotBlank()) { "title cannot be blank" }
        require(language.code.isNotBlank()) { "language code cannot be blank" }
        require(language.label.isNotBlank()) { "language label cannot be blank" }
        require(startPartId.isNotBlank()) { "startPartId cannot be blank" }
    }
}

data class StoryGenerationResult(
    val document: StoryDocument,
    val savedPath: Path,
)
