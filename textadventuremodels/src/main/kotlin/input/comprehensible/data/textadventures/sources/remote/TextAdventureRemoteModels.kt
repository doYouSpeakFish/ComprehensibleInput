package input.comprehensible.data.textadventures.sources.remote

import kotlinx.serialization.Serializable

/**
 * A response for a text adventure request.
 */
@Serializable
data class TextAdventureRemoteResponse(
    val adventureId: String,
    val title: String,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
)

/**
 * A conversation history entry to provide context for the LLM.
 */
@Serializable
data class TextAdventureHistoryMessage(
    val role: String,
    val text: String,
)

@Serializable
data class StartTextAdventureRequest(
    val learningLanguage: String,
    val translationsLanguage: String,
)

@Serializable
data class ContinueTextAdventureRequest(
    val adventureId: String,
    val learningLanguage: String,
    val translationsLanguage: String,
    val userMessage: String,
    val history: List<TextAdventureHistoryMessage>,
)
