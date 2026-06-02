package input.comprehensible.data.sources

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureParagraphRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse

class FakeTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    data class ScriptedAdventure(
        val scenario: TextAdventureRemoteResponse,
        val aiResponses: List<TextAdventureMessageRemoteResponse>,
    )

    private val scriptedAdventures = ArrayDeque<ScriptedAdventure>()
    private val aiResponsesByAdventureId = mutableMapOf<String, ArrayDeque<TextAdventureMessageRemoteResponse>>()
    private var userMessageCounter = 0

    fun enqueueAdventure(script: ScriptedAdventure) {
        scriptedAdventures.add(script)
    }

    override suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
        sessionToken: String,
    ): TextAdventureRemoteResponse {
        val script = scriptedAdventures.removeFirstOrNull()
            ?: error("No scripted adventures available")
        val adventureId = script.scenario.adventureId
        aiResponsesByAdventureId[adventureId] = ArrayDeque(script.aiResponses)
        return script.scenario
    }

    override suspend fun createUserMessage(
        adventureId: String,
        parentMessageId: String,
        text: String,
        sessionToken: String,
    ): TextAdventureMessageRemoteResponse = TextAdventureMessageRemoteResponse(
        id = "user-msg-${userMessageCounter++}",
        parentId = parentMessageId,
        type = "user",
        sender = "user",
        isEnding = false,
        paragraphs = listOf(
            TextAdventureParagraphRemoteResponse(
                sentences = listOf(text),
                translatedSentences = listOf(text),
            )
        ),
    )

    override suspend fun createAiMessage(
        adventureId: String,
        parentMessageId: String,
        sessionToken: String,
    ): TextAdventureMessageRemoteResponse {
        val responses = aiResponsesByAdventureId[adventureId]
            ?: error("No scripted AI responses available for adventure $adventureId")
        return responses.removeFirstOrNull()
            ?: error("No scripted AI responses remaining for adventure $adventureId")
    }
}
