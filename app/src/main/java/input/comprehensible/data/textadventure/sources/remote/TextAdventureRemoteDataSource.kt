package input.comprehensible.data.textadventure.sources.remote

import com.ktin.InjectedSingleton

interface TextAdventureRemoteDataSource {
    suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse

    suspend fun continueAdventure(
        adventureId: String,
        learningLanguage: String,
        translationLanguage: String,
        userResponse: String,
    ): TextAdventureRemoteResponse

    companion object : InjectedSingleton<TextAdventureRemoteDataSource>()
}

class DefaultTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    override suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse = TODO("Add the text adventure scenario source")

    override suspend fun continueAdventure(
        adventureId: String,
        learningLanguage: String,
        translationLanguage: String,
        userResponse: String,
    ): TextAdventureRemoteResponse = TODO("Add the text adventure response source")
}

data class TextAdventureRemoteResponse(
    val adventureId: String,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
)
