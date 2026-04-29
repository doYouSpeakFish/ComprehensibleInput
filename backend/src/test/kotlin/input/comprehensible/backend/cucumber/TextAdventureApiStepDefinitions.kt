package input.comprehensible.backend.cucumber

import input.comprehensible.backend.configureRouting
import input.comprehensible.backend.connectDatabase
import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse
import input.comprehensible.backend.textadventure.testing.FakeTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.testing.PostgreSqlTestDatabase
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class TextAdventureApiStepDefinitions {
    private lateinit var database: Database
    private lateinit var fakeExecutor: FakeTextAdventureStructuredPromptExecutor
    private lateinit var textAdventureService: TextAdventureGenerationService
    private val validApiKey = "test"
    private val invalidApiKey = "invalid"
    private var latestResponseStatus: HttpStatusCode? = null
    private var latestResponseBody: String = ""
    private var startedAdventureId: String = ""
    private var startedAssistantSentence: String = ""
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUpScenario() {
        database = connectDatabase(PostgreSqlTestDatabase.createConfig())
        fakeExecutor = FakeTextAdventureStructuredPromptExecutor()
        textAdventureService = TextAdventureGenerationService(
            structuredPromptExecutor = fakeExecutor,
            adventureRepository = DatabaseAdventureRepository(database = database),
        )
        latestResponseStatus = null
        latestResponseBody = ""
        startedAdventureId = ""
        startedAssistantSentence = ""
    }


    @After
    fun tearDownScenario() {
        PostgreSqlTestDatabase.reset(database)
        TransactionManager.closeAndUnregister(database)
    }

    @Given(
        "the AI will return an opening adventure titled {string} with sentence {string} and translation {string}"
    )
    fun aiReturnsOpeningAdventure(
        title: String,
        sentence: String,
        translation: String,
    ) {
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = title,
                paragraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(sentence))),
                translatedParagraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(translation))),
                isEnding = false,
            )
        )
    }

    @Given(
        "the AI will return a continuation for title {string} with sentence {string} and translation {string} that is ending {string}"
    )
    fun aiReturnsContinuation(
        title: String,
        sentence: String,
        translation: String,
        isEnding: String,
    ) {
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = title,
                paragraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(sentence))),
                translatedParagraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(translation))),
                isEnding = isEnding.toBooleanStrict(),
            )
        )
    }

    @When("I check the health endpoint")
    fun checkHealthEndpoint() {
        runAgainstApplication {
            client.get("/health")
        }
    }

    @When("I start a text adventure in {string} with translations in {string}")
    fun startAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ) {
        runAgainstApplication {
            client.post("/text-adventures/start") {
                header("X-Api-Key", validApiKey)
                contentType(ContentType.Application.Json)
                setBody(startBody(learningLanguage, translationsLanguage))
            }
        }
        val payload = json.decodeFromString<TextAdventureRemoteResponse>(latestResponseBody)
        startedAdventureId = payload.adventureId
        startedAssistantSentence = payload.sentences.single()
    }

    @When("I continue the started adventure with user message {string}")
    fun continueStartedAdventure(userMessage: String) {
        runAgainstApplication {
            client.post("/text-adventures/respond") {
                authorized(validApiKey)
                contentType(ContentType.Application.Json)
                setBody(
                    continueBody(
                        adventureId = startedAdventureId,
                        learningLanguage = "English",
                        translationsLanguage = "Spanish",
                        userMessage = userMessage,
                        priorAssistantSentence = startedAssistantSentence,
                    )
                )
            }
        }
    }

    @When("I request messages for the started adventure")
    fun requestMessagesForStartedAdventure() {
        runAgainstApplication {
            client.get("/text-adventures/$startedAdventureId/messages") {
                authorized(validApiKey)
            }
        }
    }

    @When("I start a text adventure with an invalid API key")
    fun startAdventureWithInvalidApiKey() {
        runAgainstApplication {
            client.post("/text-adventures/start") {
                authorized(invalidApiKey)
                contentType(ContentType.Application.Json)
                setBody(startBody("English", "Spanish"))
            }
        }
    }

    @When("I continue an adventure with an invalid API key")
    fun continueAdventureWithInvalidApiKey() {
        runAgainstApplication {
            client.post("/text-adventures/respond") {
                authorized(invalidApiKey)
                contentType(ContentType.Application.Json)
                setBody(
                    continueBody(
                        adventureId = "missing",
                        learningLanguage = "English",
                        translationsLanguage = "Spanish",
                        userMessage = "Go north",
                        priorAssistantSentence = "You wake up.",
                    )
                )
            }
        }
    }

    @When("I request messages with an invalid API key")
    fun requestMessagesWithInvalidApiKey() {
        runAgainstApplication {
            client.get("/text-adventures/missing/messages") {
                authorized(invalidApiKey)
            }
        }
    }

    @When("I request messages for adventure id {string}")
    fun requestMessagesForAdventureId(adventureId: String) {
        runAgainstApplication {
            client.get("/text-adventures/$adventureId/messages") {
                authorized(validApiKey)
            }
        }
    }

    @Then("the response status should be {int}")
    fun responseStatusShouldBe(expectedStatus: Int) {
        assertEquals(HttpStatusCode.fromValue(expectedStatus), latestResponseStatus)
    }

    @Then(
        "the adventure response contains title {string} with sentence {string} and translation {string}"
    )
    fun adventureResponseContains(
        expectedTitle: String,
        expectedSentence: String,
        expectedTranslation: String,
    ) {
        assertEquals(HttpStatusCode.OK, latestResponseStatus)
        val payload = json.decodeFromString<TextAdventureRemoteResponse>(latestResponseBody)
        assertEquals(expectedTitle, payload.title)
        assertEquals(
            listOf(expectedSentence),
            payload.sentences,
        )
        assertEquals(
            listOf(expectedTranslation),
            payload.translatedSentences,
        )
        assertTrue(payload.adventureId.isNotBlank())
    }

    @Then(
        "the continuation response contains sentence {string} and translation {string} with ending {string}"
    )
    fun continuationResponseContains(
        expectedSentence: String,
        expectedTranslation: String,
        expectedEnding: String,
    ) {
        assertEquals(HttpStatusCode.OK, latestResponseStatus)
        val payload = json.decodeFromString<TextAdventureRemoteResponse>(latestResponseBody)
        assertEquals(listOf(expectedSentence), payload.sentences)
        assertEquals(listOf(expectedTranslation), payload.translatedSentences)
        assertEquals(expectedEnding.toBooleanStrict(), payload.isEnding)
    }

    @Then("the messages response contains {int} AI messages")
    fun messagesResponseContainsAiMessages(expectedCount: Int) {
        assertEquals(HttpStatusCode.OK, latestResponseStatus)
        val payload = json.decodeFromString<TextAdventureMessagesRemoteResponse>(latestResponseBody)
        assertEquals(expectedCount, payload.messages.size)
        assertEquals(payload.messages.size, payload.messages.count { it.sender == "AI" })
    }

    @Then("the messages response keeps {string} before {string}")
    fun messagesResponseKeepsOrder(
        firstSentence: String,
        secondSentence: String,
    ) {
        assertEquals(HttpStatusCode.OK, latestResponseStatus)
        val payload = json.decodeFromString<TextAdventureMessagesRemoteResponse>(latestResponseBody)
        assertEquals(firstSentence, payload.messages[0].paragraphs.single().sentences.single())
        assertEquals(secondSentence, payload.messages[1].paragraphs.single().sentences.single())
    }

    private fun runAgainstApplication(
        block: suspend ApplicationTestBuilder.() -> HttpResponse,
    ) {
        runBlocking {
            testApplication {
                application {
                    configureRouting(
                        textAdventureService = textAdventureService,
                        appApiKey = validApiKey,
                    )
                }
                val response = block()
                storeLatestResponse(response)
            }
        }
    }

    private fun HttpRequestBuilder.authorized(apiKey: String) {
        header("X-Api-Key", apiKey)
    }

    private fun startBody(learningLanguage: String, translationsLanguage: String): String =
        """{"learningLanguage":"$learningLanguage","translationsLanguage":"$translationsLanguage"}"""

    private fun continueBody(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        priorAssistantSentence: String,
    ): String =
        """
        {
          "adventureId":"$adventureId",
          "learningLanguage":"$learningLanguage",
          "translationsLanguage":"$translationsLanguage",
          "userMessage":"$userMessage",
          "history":[
            {"role":"assistant","text":"$priorAssistantSentence"},
            {"role":"user","text":"$userMessage"}
          ]
        }
        """.trimIndent()

    private suspend fun storeLatestResponse(response: HttpResponse) {
        latestResponseStatus = response.status
        latestResponseBody = response.bodyAsText()
    }
}
