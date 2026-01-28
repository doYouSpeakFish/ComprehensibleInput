package input.comprehensible.data.textadventures.sources.remote

class DefaultTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    override suspend fun startAdventure(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse {
        TODO("Implement text adventure generation")
    }

    override suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
    ): TextAdventureRemoteResponse {
        TODO("Implement text adventure response generation")
    }
}
