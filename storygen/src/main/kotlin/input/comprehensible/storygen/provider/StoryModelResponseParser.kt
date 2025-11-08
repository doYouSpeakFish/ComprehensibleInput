package input.comprehensible.storygen.provider

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal class StoryModelResponseParser(
    private val json: Json = Json { ignoreUnknownKeys = true; explicitNulls = false },
) {
    fun parse(raw: String): StoryModelSegment {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            throw StoryModelClientException("Story provider returned an empty response")
        }
        val dto = try {
            json.decodeFromString(ResponseDto.serializer(), trimmed)
        } catch (error: SerializationException) {
            throw StoryModelClientException("Story provider response was not valid JSON", error)
        }
        return StoryModelSegment(
            title = dto.title.trim(),
            narrative = dto.narrative.trim(),
            isEnding = dto.isEnding,
            choices = dto.choices.orEmpty().map { choice ->
                StoryModelChoice(
                    prompt = choice.prompt.trim(),
                    summary = choice.summary.trim(),
                )
            },
        )
    }

    @Serializable
    private data class ResponseDto(
        val title: String,
        val narrative: String,
        @SerialName("isEnding") val isEnding: Boolean,
        val choices: List<ChoiceDto>? = null,
    )

    @Serializable
    private data class ChoiceDto(
        val prompt: String,
        val summary: String,
    )
}
