package input.comprehensible.data.sources

import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureHistoryMessage
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import java.util.UUID

class FakeTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    data class ScriptedAdventure(
        val scenario: TextAdventureRemoteResponse,
        val responses: List<TextAdventureRemoteResponse>,
    )

    private val scriptedAdventures = ArrayDeque<ScriptedAdventure>()
    private val responsesByAdventureId = mutableMapOf<String, ArrayDeque<TextAdventureRemoteResponse>>()

    var startAdventureException: Exception? = null
    var respondToUserException: Exception? = null

    fun enqueueAdventure(script: ScriptedAdventure) {
        scriptedAdventures.add(script)
    }

    override fun generateAdventureId(): String = UUID.randomUUID().toString()

    override suspend fun startAdventure(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse {
        startAdventureException?.let { throw it }
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
        respondToUserException?.let { throw it }
        val responses = responsesByAdventureId[adventureId]
            ?: error("No scripted responses available for adventure $adventureId")
        return responses.removeFirstOrNull()
            ?: error("No scripted responses remaining for adventure $adventureId")
    }
}
