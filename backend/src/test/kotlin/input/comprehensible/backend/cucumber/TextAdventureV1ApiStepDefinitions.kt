package input.comprehensible.backend.cucumber

import input.comprehensible.backend.AccountService
import input.comprehensible.backend.AccountsDao
import input.comprehensible.backend.InsertAccountResult
import input.comprehensible.backend.configureRouting
import input.comprehensible.backend.connectDatabase
import input.comprehensible.backend.email.EmailDataSource
import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse
import input.comprehensible.backend.textadventure.testing.FakeTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.testing.PostgreSqlTestDatabase
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.mindrot.jbcrypt.BCrypt

class TextAdventureV1ApiStepDefinitions {
    private lateinit var database: Database
    private lateinit var fakeExecutor: FakeTextAdventureStructuredPromptExecutor
    private lateinit var textAdventureService: TextAdventureGenerationService
    private lateinit var accountService: AccountService
    private lateinit var accountsDao: AccountsDao

    private var userAToken: String = ""
    private var userBToken: String = ""
    private var activeToken: String = ""
    private var isAuthenticated: Boolean = true
    private var latestResponseStatus: HttpStatusCode? = null
    private var latestResponseBody: String = ""
    private var userAAdventureId: String = ""
    private val userAAdventureIds: MutableList<String> = mutableListOf()
    private var userBAdventureId: String = ""
    private var lastSubmittedPlayerMessage: String = ""


    @Before("@v1")
    fun setUpScenario() {
        database = connectDatabase(PostgreSqlTestDatabase.createConfig())
        fakeExecutor = FakeTextAdventureStructuredPromptExecutor()
        textAdventureService = TextAdventureGenerationService(
            structuredPromptExecutor = fakeExecutor,
            adventureRepository = DatabaseAdventureRepository(database = database),
        )
        accountService = AccountService(
            database = database,
            emailDataSource = object : EmailDataSource {
                override suspend fun sendEmail(
                    to: String,
                    subject: String,
                    textBody: String,
                ) = Unit
            },
        )
        accountsDao = AccountsDao(database)

        userAToken = createVerifiedUserAndToken("a@example.com")
        userBToken = createVerifiedUserAndToken("b@example.com")
        activeToken = userAToken
        isAuthenticated = true

        latestResponseStatus = null
        latestResponseBody = ""
        userAAdventureId = ""
        userAAdventureIds.clear()
        userBAdventureId = ""
        lastSubmittedPlayerMessage = ""
    }

    @After("@v1")
    fun tearDownScenario() {
        PostgreSqlTestDatabase.reset(database)
        TransactionManager.closeAndUnregister(database)
    }

    @Given("I am authenticated as a user account")
    fun authenticatedAsUserA() {
        activeToken = userAToken
        isAuthenticated = true
    }

    @Given("I am authenticated as user B")
    fun authenticatedAsUserB() {
        activeToken = userBToken
        isAuthenticated = true
    }

    @Given("I am not authenticated")
    fun unauthenticated() {
        isAuthenticated = false
    }

    @Given("user A has an adventure")
    @Given("I have an existing adventure")
    fun userAHasAdventure() {
        enqueueNarratorResponse(title = "Lantern Trail", sentence = "Hola", translation = "Hello")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"es","translationLanguage":"en"}""")
            }
        }
        userAAdventureId = extractJsonString(latestResponseBody, "adventureId")
        userAAdventureIds.add(userAAdventureId)
    }

    @Given("user B has an adventure")
    fun userBHasAdventure() {
        enqueueNarratorResponse(title = "Forest Run", sentence = "Bonjour", translation = "Hello")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorized(userBToken)
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"fr","translationLanguage":"en"}""")
            }
        }
        userBAdventureId = extractJsonString(latestResponseBody, "adventureId")
    }

    @Given("I have an existing adventure with messages")
    fun haveExistingAdventureWithMessages() {
        userAHasAdventure()
        enqueueNarratorResponse(title = "Lantern Trail", sentence = "Sigue recto", translation = "Keep going")
        runAgainstApplication {
            client.post("/v1/adventures/$userAAdventureId/messages") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"playerText":"Camino adelante"}""")
            }
        }
    }

    @Given("user A has 2 adventures")
    fun userAHasTwoAdventures() {
        userAHasAdventure()
        enqueueNarratorResponse(title = "Moon Cave", sentence = "Miras alrededor", translation = "You look around")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"es","translationLanguage":"en"}""")
            }
        }
        userAAdventureIds.add(extractJsonString(latestResponseBody, "adventureId"))
    }

    @Given("I have 3 adventures")
    fun iHaveThreeAdventures() {
        userAHasTwoAdventures()
        enqueueNarratorResponse(title = "River Path", sentence = "Sigues el río", translation = "You follow the river")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"es","translationLanguage":"en"}""")
            }
        }
        userAAdventureIds.add(extractJsonString(latestResponseBody, "adventureId"))
    }

    @Given("the adventure has prior narrator and player turns stored in the database")
    fun adventureHasPriorTurns() {
        if (userAAdventureId.isBlank()) {
            userAHasAdventure()
        }
        enqueueNarratorResponse(title = "Lantern Trail", sentence = "Escuchas un ruido", translation = "You hear a noise")
        runAgainstApplication {
            client.post("/v1/adventures/$userAAdventureId/messages") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"playerText":"Miro detrás de mí"}""")
            }
        }
    }

    @Given("I have an adventure that is ended")
    fun iHaveAnAdventureThatIsEnded() {
        if (userAAdventureId.isBlank()) {
            userAHasAdventure()
        }
        enqueueNarratorResponse(
            title = "Lantern Trail",
            sentence = "Fin de la historia",
            translation = "The story ends",
            isEnding = true,
        )
        runAgainstApplication {
            client.post("/v1/adventures/$userAAdventureId/messages") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"playerText":"Acepto mi destino"}""")
            }
        }
    }

    @Given("I have an existing adventure with 5 turns")
    fun iHaveExistingAdventureWithFiveTurns() {
        if (userAAdventureId.isBlank()) {
            userAHasAdventure()
        }
        repeat(4) { turn ->
            enqueueNarratorResponse(
                title = "Lantern Trail",
                sentence = "Respuesta del narrador ${turn + 1}",
                translation = "Narrator reply ${turn + 1}",
            )
            runAgainstApplication {
                client.post("/v1/adventures/$userAAdventureId/messages") {
                    authorized(userAToken)
                    contentType(ContentType.Application.Json)
                    setBody("""{"playerText":"Acción del jugador ${turn + 1}"}""")
                }
            }
        }
    }

    @Given("user B has 1 adventure")
    fun userBHasOneAdventure() {
        userBHasAdventure()
    }

    @Given("I am authenticated as user A")
    fun authenticatedAsUserAExplicitly() {
        authenticatedAsUserA()
    }

    @When("I create a text adventure with learning language {string} and translation language {string}")
    fun createAdventure(learningLanguage: String, translationLanguage: String) {
        enqueueNarratorResponse(title = "Lantern Trail", sentence = "Hola", translation = "Hello")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorizedIfPresent()
                contentType(ContentType.Application.Json)
                setBody(
                    """
                    {
                      "learningLanguage":"$learningLanguage",
                      "translationLanguage":"$translationLanguage"
                    }
                    """.trimIndent(),
                )
            }
        }
    }

    @When("user A lists adventures")
    fun userAListsAdventures() {
        runAgainstApplication {
            client.get("/v1/adventures") {
                authorized(userAToken)
            }
        }
    }

    @When("I post a new player message to user A adventure")
    fun postMessageToUserAAdventureFromUserB() {
        runAgainstApplication {
            client.post("/v1/adventures/$userAAdventureId/messages") {
                authorized(userBToken)
                contentType(ContentType.Application.Json)
                setBody("""{"playerText":"Go north"}""")
            }
        }
    }


    @When("I post a new player message to unknown adventure id {string}")
    fun postMessageToUnknownAdventure(adventureId: String) {
        runAgainstApplication {
            client.post("/v1/adventures/$adventureId/messages") {
                authorizedIfPresent()
                contentType(ContentType.Application.Json)
                setBody("""{"playerText":"Go north"}""")
            }
        }
    }

    @When("I fetch messages for user A adventure")
    fun fetchMessagesForUserAAdventure() {
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId/messages") {
                authorizedIfPresent()
            }
        }
    }

    @When("I delete user A adventure")
    fun deleteUserAAdventure() {
        runAgainstApplication {
            client.delete("/v1/adventures/$userAAdventureId") {
                authorizedIfPresent()
            }
        }
    }

    @When("I delete that adventure")
    fun deleteThatAdventure() {
        runAgainstApplication {
            client.delete("/v1/adventures/$userAAdventureId") {
                authorizedIfPresent()
            }
        }
    }

    @When("I delete all my adventures")
    fun deleteAllMyAdventures() {
        runAgainstApplication {
            client.delete("/v1/adventures") {
                authorizedIfPresent()
            }
        }
    }


    @When("I post a new player message {string} to the adventure messages collection")
    fun postPlayerMessageToAdventure(message: String) {
        if (userAAdventureId.isBlank()) {
            userAHasAdventure()
        }
        lastSubmittedPlayerMessage = message
        enqueueNarratorResponse(title = "Lantern Trail", sentence = "Siguiente escena", translation = "Next scene")
        runAgainstApplication {
            client.post("/v1/adventures/$userAAdventureId/messages") {
                authorizedIfPresent()
                contentType(ContentType.Application.Json)
                setBody("""{"playerText":"$message"}""")
            }
        }
    }



    @When("I fetch that adventure by id")
    fun fetchAdventureById() {
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId") {
                authorizedIfPresent()
            }
        }
    }

    @When("I fetch user A adventure by id")
    fun fetchUserAAdventureById() {
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId") {
                authorizedIfPresent()
            }
        }
    }


    @When("I create a text adventure with invalid language payload")
    fun createAdventureWithInvalidLanguagePayload() {
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorizedIfPresent()
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"","translationLanguage":"en"}""")
            }
        }
    }

    @When("I post an empty player message to the adventure messages collection")
    fun postEmptyPlayerMessage() {
        if (userAAdventureId.isBlank()) {
            userAHasAdventure()
        }
        runAgainstApplication {
            client.post("/v1/adventures/$userAAdventureId/messages") {
                authorizedIfPresent()
                contentType(ContentType.Application.Json)
                setBody("""{"playerText":""}""")
            }
        }
    }


    @When("I list adventures")
    fun listAdventures() {
        runAgainstApplication {
            client.get("/v1/adventures") {
                authorizedIfPresent()
            }
        }
    }

    @When("I fetch adventure messages")
    fun fetchAdventureMessages() {
        if (userAAdventureId.isBlank()) {
            userAHasAdventure()
        }
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId/messages") {
                authorizedIfPresent()
            }
        }
    }

    @Then("the response status is {int}")
    fun responseStatusShouldBe(expectedStatus: Int) {
        assertEquals(HttpStatusCode.fromValue(expectedStatus), latestResponseStatus)
    }

    @Then("the response includes an adventure id")
    fun responseIncludesAdventureId() {
        assertTrue(extractJsonString(latestResponseBody, "adventureId").isNotBlank())
    }

    @Then("the response includes a title")
    fun responseIncludesTitle() {
        assertTrue(extractJsonString(latestResponseBody, "title").isNotBlank())
    }

    @Then("the response includes adventure status {string}")
    fun responseIncludesAdventureStatus(expectedStatus: String) {
        assertTrue(expectedStatus.isNotBlank())
        assertFalse(latestResponseBody.contains("\"status\":\"ended\""))
    }

    @Then("the response includes a latest narrator message")
    fun responseIncludesLatestNarratorMessage() {
        assertTrue(latestResponseBody.contains("sentences"))
        assertTrue(latestResponseBody.contains("translatedSentences"))
    }

    @Then("only adventures owned by user A are returned")
    fun listContainsOnlyUserAAdventures() {
        assertTrue(latestResponseBody.contains("Lantern Trail"))
        assertFalse(latestResponseBody.contains("Forest Run"))
    }


    @Then("the response includes learning language")
    fun responseIncludesLearningLanguage() {
        assertTrue(latestResponseBody.contains("learningLanguage"))
    }

    @Then("the response includes translation language")
    fun responseIncludesTranslationLanguage() {
        assertTrue(latestResponseBody.contains("translationLanguage"))
    }

    @Then("the response includes updated timestamp")
    fun responseIncludesUpdatedTimestamp() {
        assertTrue(latestResponseBody.contains("updatedAt"))
    }

    @Then("reading that adventure returns 404")
    fun readingThatAdventureReturnsNotFound() {
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId") {
                authorizedIfPresent()
            }
        }
        assertEquals(HttpStatusCode.NotFound, latestResponseStatus)
    }

    @Then("reading its messages returns 404")
    fun readingItsMessagesReturnsNotFound() {
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId/messages") {
                authorizedIfPresent()
            }
        }
        assertEquals(HttpStatusCode.NotFound, latestResponseStatus)
    }

    @Then("user A has no adventures")
    fun userAHasNoAdventures() {
        runAgainstApplication {
            client.get("/v1/adventures") {
                authorized(userAToken)
            }
        }
        assertTrue(userAAdventureIds.none { latestResponseBody.contains(it) })
    }

    @Then("user B adventure still exists")
    fun userBAdventureStillExists() {
        runAgainstApplication {
            client.get("/v1/adventures") {
                authorized(userBToken)
            }
        }
        assertTrue(userBAdventureId.isNotBlank())
        assertTrue(latestResponseBody.contains(userBAdventureId))
    }

    @Then("exactly {int} adventures are returned")
    fun exactlyNAdventuresReturned(expectedCount: Int) {
        assertEquals(expectedCount, "\"id\"".toRegex().findAll(latestResponseBody).count())
    }


    @Then("the response includes both stored player message and generated narrator message")
    fun responseIncludesStoredPlayerAndNarrator() {
        assertTrue(latestResponseBody.contains("Siguiente escena"))
    }

    @Then("the request does not require a full history payload")
    fun requestDoesNotRequireFullHistoryPayload() {
        assertEquals(HttpStatusCode.Created, latestResponseStatus)
    }

    @Then("the returned messages include the exact submitted player message text")
    fun returnedMessagesIncludeExactSubmittedPlayerText() {
        assertTrue(lastSubmittedPlayerMessage.isNotBlank())
        assertTrue(latestResponseBody.contains(lastSubmittedPlayerMessage))
    }

    @Then("messages are ordered by turn index ascending")
    fun messagesOrderedByTurn() {
        val first = latestResponseBody.indexOf("Respuesta del narrador 1")
        val last = latestResponseBody.indexOf("Respuesta del narrador 4")
        assertTrue(first >= 0)
        assertTrue(last > first)
    }

    @Then("exactly {int} messages are returned")
    fun exactlyNMessagesReturned(expectedCount: Int) {
        assertEquals(expectedCount, "\"sender\"".toRegex().findAll(latestResponseBody).count())
    }

    private fun runAgainstApplication(
        block: suspend ApplicationTestBuilder.() -> HttpResponse,
    ) {
        runBlocking {
            testApplication {
                application {
                    configureRouting(
                        textAdventureService = textAdventureService,
                        appApiKey = "unused",
                        accountService = accountService,
                    )
                }
                val response = block()
                latestResponseStatus = response.status
                latestResponseBody = response.bodyAsText()
            }
        }
    }

    private fun enqueueNarratorResponse(
        title: String,
        sentence: String,
        translation: String,
        isEnding: Boolean = false,
    ) {
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = title,
                paragraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(sentence))),
                translatedParagraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(translation))),
                isEnding = isEnding,
            ),
        )
    }

    private fun HttpRequestBuilder.authorized(token: String) {
        header("Authorization", "Bearer $token")
    }

    private fun HttpRequestBuilder.authorizedIfPresent() {
        if (isAuthenticated) {
            authorized(activeToken)
        }
    }

    private fun createVerifiedUserAndToken(email: String): String {
        val inserted = accountsDao.insertAccount(
            email = email,
            passwordHash = BCrypt.hashpw("password123", BCrypt.gensalt()),
            emailVerified = true,
            now = System.currentTimeMillis(),
        )
        check(inserted is InsertAccountResult.Inserted)
        val signInResult = accountService.signIn(email, "password123")
        return checkNotNull(signInResult.payload).accessToken
    }

    private fun extractJsonString(payload: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        return pattern.find(payload)?.groupValues?.get(1).orEmpty()
    }
}
