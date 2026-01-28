package input.comprehensible.data.textadventure.sources.remote

import input.comprehensible.data.textadventure.TextAdventureTestData

class FakeTextAdventureRemoteDataSource(
    private val testData: TextAdventureTestData,
) : TextAdventureRemoteDataSource {
    override suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse = testData.scenario

    override suspend fun continueAdventure(
        adventureId: String,
        learningLanguage: String,
        translationLanguage: String,
        userResponse: String,
    ): TextAdventureRemoteResponse = testData.nextResponse()
}
