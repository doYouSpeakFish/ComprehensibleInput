package input.comprehensible.data.sources

import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureHistoryMessage
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.CompletableDeferred

class FakeTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    data class ScriptedAdventure(
        val scenario: TextAdventureRemoteResponse,
        val responses: List<TextAdventureRemoteResponse>,
    )

    private val scriptedAdventures = ArrayDeque<ScriptedAdventure>()
    private val responsesByAdventureId = mutableMapOf<String, ArrayDeque<TextAdventureRemoteResponse>>()
    private var responseGate: CompletableDeferred<Unit>? = null

    fun enqueueAdventure(script: ScriptedAdventure) {
        scriptedAdventures.add(script)
    }

    fun holdResponses(): CompletableDeferred<Unit> {
        val gate = CompletableDeferred<Unit>()
        responseGate = gate
        return gate
    }

    override suspend fun startAdventure(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse {
        responseGate?.await()
        val script = scriptedAdventures.removeFirstOrNull()
            ?: error("No scripted adventures available")
        responsesByAdventureId[adventureId] = ArrayDeque(script.responses)
        return script.scenario.copy(adventureId = adventureId)
    }

    override suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
    ): TextAdventureRemoteResponse {
        responseGate?.await()
        val responses = responsesByAdventureId[adventureId]
            ?: error("No scripted responses available for adventure $adventureId")
        return responses.removeFirstOrNull()
            ?: error("No scripted responses remaining for adventure $adventureId")
    }
}
