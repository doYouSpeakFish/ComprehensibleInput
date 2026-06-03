package input.comprehensible.data.textadventures.sources.remote

import com.ktin.InjectedSingleton

interface TextAdventureRemoteDataSource {
    suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse

    suspend fun postUserMessage(
        adventureId: String,
        parentId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse

    suspend fun postAiMessage(
        adventureId: String,
        parentId: String,
    ): TextAdventureMessageRemoteResponse

    companion object : InjectedSingleton<TextAdventureRemoteDataSource>()
}
