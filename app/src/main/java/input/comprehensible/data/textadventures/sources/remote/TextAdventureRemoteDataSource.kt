package input.comprehensible.data.textadventures.sources.remote

import com.ktin.InjectedSingleton
import kotlinx.serialization.Serializable

interface TextAdventureRemoteDataSource {
    suspend fun startAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse

    suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
    ): TextAdventureRemoteResponse

    companion object : InjectedSingleton<TextAdventureRemoteDataSource>()
}

/**
 * A response for a text adventure request.
 */
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
