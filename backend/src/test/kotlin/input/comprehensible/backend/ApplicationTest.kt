package input.comprehensible.backend

import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse
import input.comprehensible.backend.textadventure.testing.FakeTextAdventureStructuredPromptExecutor
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
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest {
    private val fakeExecutor = FakeTextAdventureStructuredPromptExecutor()
    private val textAdventureService = TextAdventureGenerationService(fakeExecutor)
    private val json = Json { ignoreUnknownKeys = true }

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
    fun `start endpoint returns generated adventure`() = testApplication {
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
    }
}
