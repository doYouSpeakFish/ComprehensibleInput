package input.comprehensible.data.textadventures.sources.remote

import kotlinx.serialization.Serializable

/**
 * A response for a text adventure request.
 */
@Serializable
data class TextAdventureRemoteResponse(
    val messageId: String = "",
    val adventureId: String,
    val title: String,
    /** The [title] translated into the player's translation language. */
    val translatedTitle: String = "",
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
    /** The id of the catalogue image the AI chose for this adventure, if any. */
    val imageId: String? = null,
)

/**
 * Stored messages for a previously generated text adventure.
 */
@Serializable
data class TextAdventureMessagesRemoteResponse(
    val adventureId: String,
    val title: String,
    val learningLanguage: String,
    val translationsLanguage: String,
    val messages: List<TextAdventureMessageRemoteResponse>,
)

@Serializable
data class TextAdventureMessageRemoteResponse(
    val id: String,
    val parentId: String?,
    val type: String,
    val sender: String,
    val isEnding: Boolean,
    val paragraphs: List<TextAdventureParagraphRemoteResponse>,
)

@Serializable
data class TextAdventureParagraphRemoteResponse(
    val sentences: List<String>,
    val translatedSentences: List<String>,
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
data class ContinueTextAdventureRequest(
    val adventureId: String,
    val learningLanguage: String,
    val translationsLanguage: String,
    val userMessage: String,
    val history: List<TextAdventureHistoryMessage>,
)
