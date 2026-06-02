package input.comprehensible.data

import input.comprehensible.data.sources.FakeTextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse

class TextAdventuresTestData(
    private val remoteDataSource: FakeTextAdventureRemoteDataSource,
) {
    fun enqueueAdventure(
        scenario: TextAdventureRemoteResponse,
        aiResponses: List<TextAdventureMessageRemoteResponse>,
    ) {
        remoteDataSource.enqueueAdventure(
            FakeTextAdventureRemoteDataSource.ScriptedAdventure(
                scenario = scenario,
                aiResponses = aiResponses,
            )
        )
    }
}
