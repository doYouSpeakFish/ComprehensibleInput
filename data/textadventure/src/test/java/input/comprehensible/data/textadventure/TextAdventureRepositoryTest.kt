package input.comprehensible.data.textadventure

import input.comprehensible.data.textadventure.sources.local.AdventureEntity
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.local.MessageEntity
import input.comprehensible.data.textadventure.sources.local.MessageWithSentences
import input.comprehensible.data.textadventure.sources.local.SentenceEntity
import input.comprehensible.data.textadventure.sources.remote.AdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.RemoteAdventure
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureParagraphRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the repository's translation of the backend message tree into local rows, which the
 * offline-first chat scenarios cannot exercise (they keep the messages request in flight to prove
 * the cached conversation shows first).
 */
class TextAdventureRepositoryTest {

    @Test
    fun `refreshMessages replaces the local conversation with the backend's`() = runTest {
        // GIVEN the backend returns a conversation of an AI message followed by a user message
        val remote = StubRemoteDataSource(
            TextAdventureMessagesRemoteResponse(
                adventureId = "adventure-1",
                title = "Lantern Trail",
                learningLanguage = "en",
                translationsLanguage = "es",
                messages = listOf(
                    aiMessage(),
                    userMessage(),
                ),
            ),
        )
        val local = RecordingLocalDataSource()

        // WHEN the local conversation is refreshed from the backend
        val result = TextAdventureRepository(remote, local)
            .refreshMessages(token = "token", adventureId = "adventure-1")

        // THEN the local store is replaced with the backend's messages, in order
        assertTrue(result.isSuccess)
        assertEquals(listOf("adventure-1"), local.deletedAdventureIds)
        assertEquals(listOf("m1", "m2"), local.upsertedMessages.map { it.id })
        assertEquals(listOf("adventure-1", "adventure-1"), local.upsertedMessages.map { it.adventureId })
        assertEquals(listOf(0, 1), local.upsertedMessages.map { it.position })
        assertEquals(listOf("AI", "USER"), local.upsertedMessages.map { it.sender })
        assertEquals(listOf("Hello.", "World.", "I go."), local.insertedSentences.map { it.text })
        assertEquals(listOf("Hola.", "Mundo.", "Voy."), local.insertedSentences.map { it.translation })
        assertEquals(listOf(0, 1, 0), local.insertedSentences.map { it.sentenceIndex })
        assertEquals(listOf("m1", "m1", "m2"), local.insertedSentences.map { it.messageId })
    }

    @Test
    fun `sendUserMessage persists the created message after the latest one`() = runTest {
        // GIVEN a backend that accepts the user message and a local store whose latest message is at position 0
        val remote = StubRemoteDataSource(
            messages = emptyMessages(),
            userMessageResponse = userMessage(),
        )
        val local = RecordingLocalDataSource(maxPosition = 0)

        // WHEN the user sends a message
        val result = TextAdventureRepository(remote, local)
            .sendUserMessage(token = "token", adventureId = "adventure-1", parentId = "m1", text = "I go.")

        // THEN it is persisted just after the latest message and returned
        assertTrue(result.isSuccess)
        val message = result.getOrThrow()
        assertEquals("m2", message.id)
        assertEquals(AdventureMessageSender.USER, message.sender)
        assertEquals(listOf("I go."), message.paragraphs.single().sentences)
        assertEquals(listOf("m2"), local.upsertedMessages.map { it.id })
        assertEquals(listOf(1), local.upsertedMessages.map { it.position })
    }

    @Test
    fun `generateAiMessage persists the AI reply at the first position when none exist`() = runTest {
        // GIVEN a backend that returns an AI reply and a local store with no messages yet
        val remote = StubRemoteDataSource(
            messages = emptyMessages(),
            aiMessageResponse = aiMessage(),
        )
        val local = RecordingLocalDataSource(maxPosition = null)

        // WHEN the AI reply is generated
        val result = TextAdventureRepository(remote, local)
            .generateAiMessage(token = "token", adventureId = "adventure-1", parentId = "m1")

        // THEN the reply is persisted at the first position with its sentences
        assertTrue(result.isSuccess)
        val message = result.getOrThrow()
        assertEquals("m1", message.id)
        assertEquals(AdventureMessageSender.AI, message.sender)
        assertEquals(listOf(0), local.upsertedMessages.map { it.position })
        assertEquals(listOf("Hello.", "World."), local.insertedSentences.map { it.text })
    }

    private fun emptyMessages() = TextAdventureMessagesRemoteResponse(
        adventureId = "adventure-1",
        title = "Lantern Trail",
        learningLanguage = "en",
        translationsLanguage = "es",
        messages = emptyList(),
    )

    private fun aiMessage() = TextAdventureMessageRemoteResponse(
        id = "m1",
        parentId = null,
        type = "AI",
        sender = "AI",
        isEnding = false,
        paragraphs = listOf(
            TextAdventureParagraphRemoteResponse(
                sentences = listOf("Hello.", "World."),
                translatedSentences = listOf("Hola.", "Mundo."),
            ),
        ),
    )

    private fun userMessage() = TextAdventureMessageRemoteResponse(
        id = "m2",
        parentId = "m1",
        type = "user",
        sender = "user",
        isEnding = true,
        paragraphs = listOf(
            TextAdventureParagraphRemoteResponse(
                sentences = listOf("I go."),
                translatedSentences = listOf("Voy."),
            ),
        ),
    )
}

private class StubRemoteDataSource(
    private val messages: TextAdventureMessagesRemoteResponse,
    private val userMessageResponse: TextAdventureMessageRemoteResponse? = null,
    private val aiMessageResponse: TextAdventureMessageRemoteResponse? = null,
) : AdventureRemoteDataSource {
    override suspend fun getMessages(token: String, adventureId: String) = messages
    override suspend fun getAdventures(token: String): List<RemoteAdventure> = emptyList()
    override suspend fun deleteAdventure(token: String, adventureId: String) = Unit
    override suspend fun startAdventure(
        token: String,
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureRemoteResponse = error("not used")

    override suspend fun sendUserMessage(
        token: String,
        adventureId: String,
        parentId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse = requireNotNull(userMessageResponse)

    override suspend fun generateAiMessage(
        token: String,
        adventureId: String,
        parentId: String,
    ): TextAdventureMessageRemoteResponse = requireNotNull(aiMessageResponse)
}

private class RecordingLocalDataSource(
    private val maxPosition: Int? = null,
) : AdventureLocalDataSource {
    val deletedAdventureIds = mutableListOf<String>()
    val upsertedMessages = mutableListOf<MessageEntity>()
    val insertedSentences = mutableListOf<SentenceEntity>()

    override suspend fun deleteMessages(adventureId: String) {
        deletedAdventureIds += adventureId
    }

    override suspend fun maxMessagePosition(adventureId: String): Int? = maxPosition

    override suspend fun upsertMessage(message: MessageEntity) {
        upsertedMessages += message
    }

    override suspend fun insertSentences(sentences: List<SentenceEntity>) {
        insertedSentences += sentences
    }

    override fun observeAdventures(userId: String): Flow<List<AdventureEntity>> = emptyFlow()
    override suspend fun getAdventure(id: String): AdventureEntity? = null
    override suspend fun upsertAdventures(adventures: List<AdventureEntity>) = Unit
    override suspend fun upsertAdventure(adventure: AdventureEntity) = Unit
    override suspend fun deleteAdventure(id: String) = Unit
    override fun observeMessages(adventureId: String): Flow<List<MessageWithSentences>> = emptyFlow()
}
