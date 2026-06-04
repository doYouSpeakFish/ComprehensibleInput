package input.comprehensible.data.textadventures.sources.remote

import com.ktin.InjectedSingleton

interface TextAdventureRemoteDataSource {
    suspend fun startAdventure(
        token: String,
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse

    suspend fun postUserMessage(
        token: String,
        adventureId: String,
        parentId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse

    suspend fun postAiMessage(
        token: String,
        adventureId: String,
        parentId: String,
    ): TextAdventureMessageRemoteResponse

    companion object : InjectedSingleton<TextAdventureRemoteDataSource>()
}
