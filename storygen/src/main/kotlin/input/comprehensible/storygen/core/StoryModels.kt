package input.comprehensible.storygen.core

data class GeneratedStory(
    val genre: String,
    val inspirationWords: List<String>,
    val root: StoryNode,
)

data class StoryNode(
    val title: String,
    val narrative: String,
    val choices: List<StoryChoice>,
)

data class StoryChoice(
    val prompt: String,
    val summary: String,
    val next: StoryNode?,
)
