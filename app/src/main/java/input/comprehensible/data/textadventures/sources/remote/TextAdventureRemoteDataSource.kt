package input.comprehensible.data.textadventures.sources.remote

import com.ktin.InjectedSingleton

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
