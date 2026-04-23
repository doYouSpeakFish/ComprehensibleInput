package input.comprehensible.backend.textadventure

import input.comprehensible.backend.textadventure.testing.FakeTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.testing.MySqlTestDatabase
import input.comprehensible.data.textadventures.sources.remote.ContinueTextAdventureRequest
import input.comprehensible.data.textadventures.sources.remote.TextAdventureHistoryMessage
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TextAdventureGenerationServiceTest {
    private lateinit var database: Database
    private lateinit var fakeExecutor: FakeTextAdventureStructuredPromptExecutor
    private lateinit var service: TextAdventureGenerationService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        database = MySqlTestDatabase.connectAndInitialize()
        MySqlTestDatabase.reset(database)
        fakeExecutor = FakeTextAdventureStructuredPromptExecutor()
        service = TextAdventureGenerationService(
            structuredPromptExecutor = fakeExecutor,
            adventureRepository = DatabaseAdventureRepository(database = database),
        )
    }

    @Test
    fun `start adventure trims and flattens AI content`() = runTest {
        // GIVEN an AI response with whitespace and two paragraphs.
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = "  Cavern Echoes  ",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("  First. ", " Second. ")),
                    TextAdventureStructuredParagraph(sentences = listOf(" Third.  ")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf(" Primero. ", " Segundo. ")),
                    TextAdventureStructuredParagraph(sentences = listOf(" Tercero.  ")),
                ),
                isEnding = false,
            )
        )

        // WHEN a user starts a new adventure.
        val response = service.startAdventure(
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )

        // THEN the payload is normalized for the client.
        assertEquals("Cavern Echoes", response.title)
        assertEquals(listOf("First.", "Second.", "Third."), response.sentences)
        assertEquals(listOf("Primero.", "Segundo.", "Tercero."), response.translatedSentences)
        assertEquals(false, response.isEnding)
        assertEquals("text-adventure-start", fakeExecutor.invocations.single().promptName)

        // THEN the adventure is persisted in MySQL using normalized rows.
        transaction(database) {
            val adventures = AdventuresTable.selectAll().toList()
            assertEquals(1, adventures.size)
            assertEquals(response.adventureId, adventures.single()[AdventuresTable.id])
            assertEquals("Cavern Echoes", adventures.single()[AdventuresTable.title])

            val messages = AdventureMessagesTable.selectAll().toList()
            assertEquals(1, messages.size)
            assertEquals(0, messages.single()[AdventureMessagesTable.messageIndex])
            assertEquals(false, messages.single()[AdventureMessagesTable.isEnding])

            val sentences = AdventureSentencesTable
                .selectAll()
                .orderBy(AdventureSentencesTable.paragraphIndex, SortOrder.ASC)
                .orderBy(AdventureSentencesTable.sentenceIndex, SortOrder.ASC)
                .orderBy(AdventureSentencesTable.language, SortOrder.ASC)
                .toList()
            assertEquals(6, sentences.size)
        }
    }

    @Test
    fun `respond to user sends serialized history request to AI`() = runTest {
        // GIVEN a follow-up AI response.
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = "Forest Echoes",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("You move forward.")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("Avanzas.")),
                ),
                isEnding = false,
            )
        )

        // WHEN the user responds in the ongoing story.
        service.respondToUser(
            adventureId = "adv-1",
            learningLanguage = "English",
            translationsLanguage = "Spanish",
            userMessage = "Go left",
            history = listOf(
                TextAdventureHistoryMessage(role = "assistant", text = "You are in a forest."),
                TextAdventureHistoryMessage(role = "user", text = "I look around."),
            ),
        )

        // THEN the backend sends the full continue request JSON to the AI wrapper.
        val invocation = fakeExecutor.invocations.single()
        assertEquals("text-adventure-continue", invocation.promptName)
        assertTrue(invocation.systemPrompt.contains("continuing an ongoing story"))
        val request = json.decodeFromString<ContinueTextAdventureRequest>(invocation.userPrompt)
        assertEquals("adv-1", request.adventureId)
        assertEquals("Go left", request.userMessage)
        assertEquals(2, request.history.size)

        // THEN the latest adventure state is persisted in MySQL.
        transaction(database) {
            val messages = AdventureMessagesTable.selectAll().toList()
            assertEquals(1, messages.size)
            assertEquals("adv-1", messages.single()[AdventureMessagesTable.adventureId])
        }
    }

    @Test
    fun `retries when generated paragraph sentence counts mismatch`() = runTest {
        // GIVEN an invalid response first and a valid response on retry.
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = "Mismatch",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("One.", "Two.")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("Uno.")),
                ),
                isEnding = false,
            )
        )
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = "Recovered",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("One.")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("Uno.")),
                ),
                isEnding = true,
            )
        )

        // WHEN the backend asks for a start response.
        val response = service.startAdventure(
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )

        // THEN the service retries and eventually returns the valid payload.
        assertEquals("Recovered", response.title)
        assertEquals(listOf("One."), response.sentences)
        assertEquals(true, response.isEnding)
        assertEquals(2, fakeExecutor.invocations.size)

        // THEN only one valid message is persisted.
        transaction(database) {
            val messages = AdventureMessagesTable.selectAll().toList()
            assertEquals(1, messages.size)
            assertEquals(true, messages.single()[AdventureMessagesTable.isEnding])
        }
    }
}


private fun runTest(block: suspend () -> Unit) {
    kotlinx.coroutines.runBlocking {
        block()
    }
}
