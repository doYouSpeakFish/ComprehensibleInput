package input.comprehensible.data.sources

import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse

class FakeTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    data class ScriptedAdventure(
        val scenario: TextAdventureRemoteResponse,
        val responses: List<TextAdventureRemoteResponse>,
    )

    private val scriptedAdventures = ArrayDeque<ScriptedAdventure>()
    private val responsesByAdventureId = mutableMapOf<String, ArrayDeque<TextAdventureRemoteResponse>>()

    fun enqueueAdventure(script: ScriptedAdventure) {
        scriptedAdventures.add(script)
    }

    override suspend fun startAdventure(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse {
        val script = scriptedAdventures.removeFirstOrNull()
            ?: error("No scripted adventures available")
        responsesByAdventureId[adventureId] = ArrayDeque(script.responses)
        return script.scenario
    }

    override suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
    ): TextAdventureRemoteResponse {
        val responses = responsesByAdventureId[adventureId]
            ?: error("No scripted responses available for adventure $adventureId")
        return responses.removeFirstOrNull()
            ?: error("No scripted responses remaining for adventure $adventureId")
    }
}
