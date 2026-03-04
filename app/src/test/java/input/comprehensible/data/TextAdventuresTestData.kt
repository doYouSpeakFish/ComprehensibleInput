package input.comprehensible.data

import input.comprehensible.data.sources.FakeTextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse

class TextAdventuresTestData(
    private val remoteDataSource: FakeTextAdventureRemoteDataSource,
) {
    fun enqueueAdventure(
        scenario: TextAdventureRemoteResponse,
        responses: List<TextAdventureRemoteResponse>,
    ) {
        remoteDataSource.enqueueAdventure(
            FakeTextAdventureRemoteDataSource.ScriptedAdventure(
                scenario = scenario,
                responses = responses,
            )
        )
    }
}
