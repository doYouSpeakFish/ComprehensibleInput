package input.comprehensible.storygen.provider

import input.comprehensible.storygen.core.StoryModelPrompt

interface StoryModelClient {
    suspend fun requestSegment(prompt: StoryModelPrompt): StoryModelSegment
}

data class StoryModelSegment(
    val title: String,
    val narrative: String,
    val isEnding: Boolean,
    val choices: List<StoryModelChoice>,
)

data class StoryModelChoice(
    val prompt: String,
    val summary: String,
)

class StoryModelClientException(message: String, cause: Throwable? = null) : Exception(message, cause)
