package input.comprehensible.data.textadventures.sources.remote

import com.ktin.InjectedSingleton

interface TextAdventureRemoteDataSource {
    suspend fun startAdventure(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse

    suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
    ): TextAdventureRemoteResponse

    companion object : InjectedSingleton<TextAdventureRemoteDataSource>()
}

/**
 * A response for a text adventure request.
 */
data class TextAdventureRemoteResponse(
    val title: String,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
)
