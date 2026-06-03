package input.comprehensible.data.sources

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureParagraphRemoteResponse

class FakeTextAdventureRemoteDataSource : TextAdventureRemoteDataSource {
    data class ScriptedAdventure(
        val scenario: TextAdventureRemoteResponse,
        val responses: List<TextAdventureRemoteResponse>,
    )

    private val scriptedAdventures = ArrayDeque<ScriptedAdventure>()
    private val responsesByAdventureId = mutableMapOf<String, ArrayDeque<TextAdventureRemoteResponse>>()
    private var messageIdCounter = 0

    private fun nextMessageId() = "fake-message-${messageIdCounter++}"

    fun enqueueAdventure(script: ScriptedAdventure) {
        scriptedAdventures.add(script)
    }

    override suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse {
        val script = scriptedAdventures.removeFirstOrNull()
            ?: error("No scripted adventures available")
        val response = script.scenario.copy(messageId = nextMessageId())
        responsesByAdventureId[response.adventureId] = ArrayDeque(script.responses)
        return response
    }

    override suspend fun postUserMessage(
        adventureId: String,
        parentId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse = TextAdventureMessageRemoteResponse(
        id = nextMessageId(),
        parentId = parentId,
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

    override suspend fun postAiMessage(
        adventureId: String,
        parentId: String,
    ): TextAdventureMessageRemoteResponse {
        val responses = responsesByAdventureId[adventureId]
            ?: error("No scripted responses available for adventure $adventureId")
        val response = responses.removeFirstOrNull()
            ?: error("No scripted responses remaining for adventure $adventureId")
        return TextAdventureMessageRemoteResponse(
            id = nextMessageId(),
            parentId = parentId,
            type = "AI",
            sender = "AI",
            isEnding = response.isEnding,
            paragraphs = response.paragraphs.ifEmpty {
                listOf(
                    TextAdventureParagraphRemoteResponse(
                        sentences = response.sentences,
                        translatedSentences = response.translatedSentences,
                    )
                )
            },
        )
    }
}
