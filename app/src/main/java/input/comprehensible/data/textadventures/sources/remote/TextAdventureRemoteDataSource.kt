package input.comprehensible.data.textadventures.sources.remote

import com.ktin.InjectedSingleton

interface TextAdventureRemoteDataSource {
    suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
        sessionToken: String,
    ): TextAdventureRemoteResponse

    suspend fun createUserMessage(
        adventureId: String,
        parentMessageId: String,
        text: String,
        sessionToken: String,
    ): TextAdventureMessageRemoteResponse

    suspend fun createAiMessage(
        adventureId: String,
        parentMessageId: String,
        sessionToken: String,
    ): TextAdventureMessageRemoteResponse

    companion object : InjectedSingleton<TextAdventureRemoteDataSource>()
}
