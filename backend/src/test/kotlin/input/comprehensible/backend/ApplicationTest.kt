package input.comprehensible.backend

import input.comprehensible.backend.textadventure.AdventuresTable
import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse
import input.comprehensible.backend.textadventure.testing.FakeTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.testing.MySqlTestDatabase
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureParagraphRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.testcontainers.DockerClientFactory

class ApplicationTest {
    private lateinit var database: Database
    private lateinit var fakeExecutor: FakeTextAdventureStructuredPromptExecutor
    private lateinit var textAdventureService: TextAdventureGenerationService
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        assumeTrue("Docker is required for MySQL Testcontainers tests", isDockerAvailable())

        database = MySqlTestDatabase.connectAndInitialize()
        MySqlTestDatabase.reset(database)
        fakeExecutor = FakeTextAdventureStructuredPromptExecutor()
        textAdventureService = TextAdventureGenerationService(
            structuredPromptExecutor = fakeExecutor,
            adventureRepository = DatabaseAdventureRepository(database = database),
        )
    }

    @Test
    fun `health endpoint returns ok`() = testApplication {
        // GIVEN a running server with backend routing configured.
        application {
            configureRouting(
                textAdventureService = textAdventureService,
                appApiKey = "test"
            )
        }

        // WHEN the user requests the health endpoint.
        val response = client.get("/health")

        // THEN the service reports healthy status and body.
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("ok", response.bodyAsText())
    }

    @Test
    fun `start endpoint returns generated adventure and persists it`() = testApplication {
        // GIVEN a structured AI response from the fake executor.
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = " Lantern Trail ",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("  You walk on. ")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("  Caminas. ")),
                ),
                isEnding = false,
            )
        )
        application {
            configureRouting(
                textAdventureService = textAdventureService,
                appApiKey = "test"
            )
        }

        // WHEN the client asks the backend to start an adventure.
        val response = client.post("/text-adventures/start") {
            header("X-Api-Key", "test")
            contentType(ContentType.Application.Json)
            setBody("""{"learningLanguage":"English","translationsLanguage":"Spanish"}""")
        }

        // THEN the endpoint responds with the adventure payload.
        assertEquals(HttpStatusCode.OK, response.status)
        val payload = json.decodeFromString<TextAdventureRemoteResponse>(response.bodyAsText())
        assertEquals(
            TextAdventureRemoteResponse(
                adventureId = payload.adventureId,
                title = "Lantern Trail",
                sentences = listOf("You walk on."),
                translatedSentences = listOf("Caminas."),
                isEnding = false,
            ),
            payload,
        )

        // THEN the adventure row exists in the real MySQL repository.
        transaction(database) {
            val rows = AdventuresTable.selectAll().toList()
            assertEquals(1, rows.size)
            assertEquals(payload.adventureId, rows.single()[AdventuresTable.id])
        }
    }

    @Test
    fun `messages endpoint returns all stored messages for an adventure`() = testApplication {
        // GIVEN two generated AI responses for the same adventure.
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = "Lantern Trail",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("You wake up.")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("Despiertas.")),
                ),
                isEnding = false,
            )
        )
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = "Lantern Trail",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("You walk north.")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("Caminas al norte.")),
                ),
                isEnding = true,
            )
        )
        application {
            configureRouting(
                textAdventureService = textAdventureService,
                appApiKey = "test"
            )
        }

        // WHEN the client starts and continues an adventure.
        val startedAdventure = client.post("/text-adventures/start") {
            header("X-Api-Key", "test")
            contentType(ContentType.Application.Json)
            setBody("""{"learningLanguage":"English","translationsLanguage":"Spanish"}""")
        }
        val startedPayload = json.decodeFromString<TextAdventureRemoteResponse>(startedAdventure.bodyAsText())
        client.post("/text-adventures/respond") {
            header("X-Api-Key", "test")
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "adventureId":"${startedPayload.adventureId}",
                  "learningLanguage":"English",
                  "translationsLanguage":"Spanish",
                  "userMessage":"Go north",
                  "history":[
                    {"role":"assistant","text":"You wake up."},
                    {"role":"user","text":"Go north"}
                  ]
                }
                """.trimIndent()
            )
        }

        // THEN the messages endpoint returns both stored messages in order.
        val response = client.get("/text-adventures/${startedPayload.adventureId}/messages") {
            header("X-Api-Key", "test")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val messagesPayload = json.decodeFromString<TextAdventureMessagesRemoteResponse>(response.bodyAsText())
        assertEquals(
            TextAdventureMessagesRemoteResponse(
                adventureId = startedPayload.adventureId,
                title = "Lantern Trail",
                learningLanguage = "English",
                translationsLanguage = "Spanish",
                messages = listOf(
                    TextAdventureMessageRemoteResponse(
                        sender = "AI",
                        isEnding = false,
                        paragraphs = listOf(
                            TextAdventureParagraphRemoteResponse(
                                sentences = listOf("You wake up."),
                                translatedSentences = listOf("Despiertas."),
                            )
                        ),
                    ),
                    TextAdventureMessageRemoteResponse(
                        sender = "AI",
                        isEnding = true,
                        paragraphs = listOf(
                            TextAdventureParagraphRemoteResponse(
                                sentences = listOf("You walk north."),
                                translatedSentences = listOf("Caminas al norte."),
                            )
                        ),
                    ),
                ),
            ),
            messagesPayload,
        )
    }

    @Test
    fun `messages endpoint returns not found for unknown adventure`() = testApplication {
        // GIVEN a running server.
        application {
            configureRouting(
                textAdventureService = textAdventureService,
                appApiKey = "test"
            )
        }

        // WHEN the client requests messages for an unknown adventure.
        val response = client.get("/text-adventures/missing/messages") {
            header("X-Api-Key", "test")
        }

        // THEN the endpoint responds with not found.
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `start endpoint returns unauthorized for invalid API key`() = testApplication {
        // GIVEN a structured AI response from the fake executor.
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = " Lantern Trail ",
                paragraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("  You walk on. ")),
                ),
                translatedParagraphs = listOf(
                    TextAdventureStructuredParagraph(sentences = listOf("  Caminas. ")),
                ),
                isEnding = false,
            )
        )
        application {
            configureRouting(
                textAdventureService = textAdventureService,
                appApiKey = "test"
            )
        }

        // WHEN the client asks the backend to start an adventure with an invalid api key.
        val response = client.post("/text-adventures/start") {
            header("X-Api-Key", "invalid")
            contentType(ContentType.Application.Json)
            setBody("""{"learningLanguage":"English","translationsLanguage":"Spanish"}""")
        }

        // THEN the endpoint responds with an unauthorized status.
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        transaction(database) {
            assertEquals(0, AdventuresTable.selectAll().count())
        }
    }
}

private fun isDockerAvailable(): Boolean = try {
    DockerClientFactory.instance().isDockerAvailable
} catch (_: Throwable) {
    false
}
