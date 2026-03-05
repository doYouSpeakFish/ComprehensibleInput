package input.comprehensible.data.textadventures

import input.comprehensible.data.sources.FakeTextAdventureRemoteDataSource
import input.comprehensible.data.sources.FakeTextAdventuresLocalDataSource
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender
import input.comprehensible.data.textadventures.sources.local.TextAdventureEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageSentenceView
import input.comprehensible.data.textadventures.sources.local.TextAdventureSentenceEntity
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextAdventuresRepositoryTest {

    private val localDataSource = FakeTextAdventuresLocalDataSource()
    private val remoteDataSource = FakeTextAdventureRemoteDataSource()
    private val repository = TextAdventuresRepository(
        localDataSource = localDataSource,
        remoteDataSource = remoteDataSource,
        clock = { 1000L },
    )

    // region getAdventures

    @Test
    fun `getAdventures emits error when local data source throws`() = runTest {
        // GIVEN the local data source throws an exception
        localDataSource.summariesFlow = flow { throw RuntimeException("db error") }

        // WHEN observing adventures
        val result = repository.getAdventures().first()

        // THEN the result is an error
        assertEquals(TextAdventuresListResult.Error, result)
    }

    // endregion

    // region getAdventure

    @Test
    fun `getAdventure emits error when rows are empty`() = runTest {
        // GIVEN the local data source returns empty rows
        localDataSource.sentenceRowsFlow = flow { emit(emptyList()) }

        // WHEN observing an adventure
        val result = repository.getAdventure("adventure-1").first()

        // THEN the result is an error
        assertEquals(TextAdventureResult.Error, result)
    }

    @Test
    fun `getAdventure emits error when local data source throws`() = runTest {
        // GIVEN the local data source throws an exception
        localDataSource.sentenceRowsFlow = flow { throw RuntimeException("db error") }

        // WHEN observing an adventure
        val result = repository.getAdventure("adventure-1").first()

        // THEN the result is an error
        assertEquals(TextAdventureResult.Error, result)
    }

    @Test
    fun `getAdventure emits success when rows are present`() = runTest {
        // GIVEN the local data source returns sentence rows
        localDataSource.sentenceRowsFlow = flow {
            emit(
                listOf(
                    TextAdventureMessageSentenceView(
                        adventureId = "adventure-1",
                        title = "Test Adventure",
                        learningLanguage = "de",
                        translationLanguage = "en",
                        messageIndex = 0,
                        sender = TextAdventureMessageSender.AI,
                        isEnding = false,
                        paragraphIndex = 0,
                        sentenceIndex = 0,
                        language = "de",
                        text = "Hallo",
                    ),
                )
            )
        }

        // WHEN observing an adventure
        val result = repository.getAdventure("adventure-1").first()

        // THEN the result is a success
        assertTrue(result is TextAdventureResult.Success)
        val adventure = (result as TextAdventureResult.Success).adventure
        assertEquals("adventure-1", adventure.id)
        assertEquals("Test Adventure", adventure.title)
    }

    // endregion

    // region respondToAdventure

    @Test
    fun `respondToAdventure returns early when adventure not found`() = runTest {
        // GIVEN no adventure exists in the local data source
        // WHEN responding to a non-existent adventure
        repository.respondToAdventure("nonexistent", "hello")

        // THEN no remote call is made (no responses enqueued, so it would throw if called)
        // AND no messages are inserted (verified by checking local data source state)
        val messages = localDataSource.getMessagesSnapshot("nonexistent")
        assertTrue(messages.isEmpty())
    }

    @Test
    fun `respondToAdventure uses index 0 when no previous messages exist`() = runTest {
        // GIVEN an adventure exists but has no messages yet
        localDataSource.insertAdventure(
            TextAdventureEntity(
                id = "adventure-1",
                title = "Test",
                learningLanguage = "de",
                translationLanguage = "en",
                createdAt = 500L,
                updatedAt = 500L,
            )
        )
        remoteDataSource.enqueueAdventure(
            FakeTextAdventureRemoteDataSource.ScriptedAdventure(
                scenario = TextAdventureRemoteResponse(
                    adventureId = "adventure-1",
                    title = "Test",
                    sentences = listOf("Scenario"),
                    translatedSentences = listOf("Scenario translated"),
                    isEnding = false,
                ),
                responses = listOf(
                    TextAdventureRemoteResponse(
                        adventureId = "adventure-1",
                        title = "Test",
                        sentences = listOf("Response"),
                        translatedSentences = listOf("Response translated"),
                        isEnding = false,
                    ),
                ),
            )
        )
        // Start the adventure so remote data source has a response ready
        remoteDataSource.startAdventure("de", "en")

        // WHEN responding to the adventure
        repository.respondToAdventure("adventure-1", "Hallo")

        // THEN getLatestMessageIndex returned null, so nextIndex = (-1) + 1 = 0
        // AND the user message is inserted at index 0
        val messages = localDataSource.getMessagesSnapshot("adventure-1")
        assertEquals(0, messages.first { it.sender == TextAdventureMessageSender.USER }.messageIndex)
        // AND the AI response is inserted at index 1
        assertEquals(1, messages.first { it.sender == TextAdventureMessageSender.AI }.messageIndex)
    }

    // endregion

    // region buildHistory (tested indirectly through respondToAdventure)

    @Test
    fun `respondToAdventure builds history filtering blank sentences`() = runTest {
        // GIVEN an adventure with a message that has blank sentence text
        localDataSource.insertAdventure(
            TextAdventureEntity(
                id = "adventure-1",
                title = "Test",
                learningLanguage = "de",
                translationLanguage = "en",
                createdAt = 500L,
                updatedAt = 500L,
            )
        )
        // Insert an AI message at index 0 with a blank sentence
        localDataSource.insertMessageAndSentences(
            TextAdventureMessageEntity(
                adventureId = "adventure-1",
                sender = TextAdventureMessageSender.AI,
                isEnding = false,
                createdAt = 500L,
                messageIndex = 0,
            ),
            listOf(
                TextAdventureSentenceEntity(
                    adventureId = "adventure-1",
                    messageIndex = 0,
                    paragraphIndex = 0,
                    sentenceIndex = 0,
                    language = "de",
                    text = "   ",
                ),
            ),
        )
        // Insert a user message at index 1 with valid text
        localDataSource.insertMessageAndSentences(
            TextAdventureMessageEntity(
                adventureId = "adventure-1",
                sender = TextAdventureMessageSender.USER,
                isEnding = false,
                createdAt = 600L,
                messageIndex = 1,
            ),
            listOf(
                TextAdventureSentenceEntity(
                    adventureId = "adventure-1",
                    messageIndex = 1,
                    paragraphIndex = 0,
                    sentenceIndex = 0,
                    language = "de",
                    text = "Hallo Welt",
                ),
            ),
        )
        remoteDataSource.enqueueAdventure(
            FakeTextAdventureRemoteDataSource.ScriptedAdventure(
                scenario = TextAdventureRemoteResponse(
                    adventureId = "adventure-1",
                    title = "Test",
                    sentences = listOf("Scenario"),
                    translatedSentences = listOf("Scenario translated"),
                    isEnding = false,
                ),
                responses = listOf(
                    TextAdventureRemoteResponse(
                        adventureId = "adventure-1",
                        title = "Test",
                        sentences = listOf("AI reply"),
                        translatedSentences = listOf("AI reply translated"),
                        isEnding = false,
                    ),
                ),
            )
        )
        remoteDataSource.startAdventure("de", "en")

        // WHEN responding to the adventure (this internally calls buildHistory)
        repository.respondToAdventure("adventure-1", "Wie geht es?")

        // THEN the call succeeds without error (buildHistory filtered the blank message)
        // AND the new messages are inserted
        val allMessages = localDataSource.getMessagesSnapshot("adventure-1")
        assertEquals(4, allMessages.size)
    }

    @Test
    fun `respondToAdventure builds history with no sentences for a message`() = runTest {
        // GIVEN an adventure with a message that has no matching sentences
        localDataSource.insertAdventure(
            TextAdventureEntity(
                id = "adventure-1",
                title = "Test",
                learningLanguage = "de",
                translationLanguage = "en",
                createdAt = 500L,
                updatedAt = 500L,
            )
        )
        // Insert an AI message at index 0 with sentences in a different language only
        localDataSource.insertMessageAndSentences(
            TextAdventureMessageEntity(
                adventureId = "adventure-1",
                sender = TextAdventureMessageSender.AI,
                isEnding = false,
                createdAt = 500L,
                messageIndex = 0,
            ),
            listOf(
                TextAdventureSentenceEntity(
                    adventureId = "adventure-1",
                    messageIndex = 0,
                    paragraphIndex = 0,
                    sentenceIndex = 0,
                    language = "en",
                    text = "Hello",
                ),
            ),
        )
        remoteDataSource.enqueueAdventure(
            FakeTextAdventureRemoteDataSource.ScriptedAdventure(
                scenario = TextAdventureRemoteResponse(
                    adventureId = "adventure-1",
                    title = "Test",
                    sentences = listOf("Scenario"),
                    translatedSentences = listOf("Scenario translated"),
                    isEnding = false,
                ),
                responses = listOf(
                    TextAdventureRemoteResponse(
                        adventureId = "adventure-1",
                        title = "Test",
                        sentences = listOf("Reply"),
                        translatedSentences = listOf("Reply translated"),
                        isEnding = false,
                    ),
                ),
            )
        )
        remoteDataSource.startAdventure("de", "en")

        // WHEN responding (buildHistory finds no learning-language sentences for message 0)
        repository.respondToAdventure("adventure-1", "Hallo")

        // THEN the call succeeds (the message with no matching sentences is filtered out)
        val allMessages = localDataSource.getMessagesSnapshot("adventure-1")
        assertEquals(3, allMessages.size)
    }

    // endregion
}
