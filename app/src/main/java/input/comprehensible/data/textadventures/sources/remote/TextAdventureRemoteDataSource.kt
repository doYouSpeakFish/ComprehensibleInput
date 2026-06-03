package input.comprehensible.data.textadventures.sources.remote

import com.ktin.InjectedSingleton

interface TextAdventureRemoteDataSource {
    suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
        token: String,
    ): TextAdventureRemoteResponse

    suspend fun postUserMessage(
        adventureId: String,
        parentId: String,
        text: String,
        token: String,
    ): TextAdventureMessageRemoteResponse

    suspend fun postAiMessage(
        adventureId: String,
        parentId: String,
        token: String,
    ): TextAdventureMessageRemoteResponse

    companion object : InjectedSingleton<TextAdventureRemoteDataSource>()
}
