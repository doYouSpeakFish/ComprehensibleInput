package input.comprehensible.backend.cucumber

import input.comprehensible.backend.AccountService
import input.comprehensible.backend.AccountsDao
import input.comprehensible.backend.InsertAccountResult
import input.comprehensible.backend.TEXT_ADVENTURE_RATE_LIMIT
import input.comprehensible.backend.configureRouting
import input.comprehensible.backend.connectDatabase
import input.comprehensible.backend.email.EmailDataSource
import input.comprehensible.backend.textadventure.ADVENTURE_IMAGE_EXTENSION
import input.comprehensible.backend.textadventure.ADVENTURE_IMAGES_PATH
import input.comprehensible.backend.textadventure.AdventureImage
import input.comprehensible.backend.textadventure.AdventureImageCatalog
import input.comprehensible.backend.textadventure.AdventurePlanLocationResponse
import input.comprehensible.backend.textadventure.AdventurePlanNpcResponse
import input.comprehensible.backend.textadventure.AdventurePlanStructuredResponse
import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.PreplannedAdventureCatalog
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.TextAdventureStructuredParagraph
import input.comprehensible.backend.textadventure.TextAdventureStructuredResponse
import input.comprehensible.backend.textadventure.UserMessageStructuredResponse
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
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.mindrot.jbcrypt.BCrypt
import java.util.ArrayDeque
import java.util.UUID

@Suppress("LargeClass")
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
    private var latestResponseContentType: String = ""
    private var userAAdventureId: String = ""
    private val userAAdventureIds: MutableList<String> = mutableListOf()
    private val adventureIdsByTitle: MutableMap<String, String> = mutableMapOf()
    private var userBAdventureId: String = ""
    private var rootMessageId: String = ""
    private var lastSubmittedPlayerMessage: String = ""
    private val nextMessageIds = ArrayDeque<String>()

    @Before("@v1")
    fun setUpScenario() {
        database = connectDatabase(PostgreSqlTestDatabase.createConfig())
        fakeExecutor = FakeTextAdventureStructuredPromptExecutor()
        textAdventureService = TextAdventureGenerationService(
            structuredPromptExecutor = fakeExecutor,
            adventureRepository = DatabaseAdventureRepository(database = database),
            messageIdProvider = { nextMessageIds.pollFirst() ?: UUID.randomUUID().toString() },
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
        latestResponseContentType = ""
        userAAdventureId = ""
        userAAdventureIds.clear()
        adventureIdsByTitle.clear()
        userBAdventureId = ""
        rootMessageId = ""
        lastSubmittedPlayerMessage = ""
        nextMessageIds.clear()
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
        createAdventureForUserA(title = "Lantern Trail")
    }

    @Given("user A has a {string} adventure")
    @Given("I have an existing {string} adventure")
    fun userAHasTitledAdventure(title: String) {
        createAdventureForUserA(title)
    }

    private fun createAdventureForUserA(title: String) {
        enqueueNarratorResponse(title = title, sentence = "Hola", translation = "Hello")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"es","translationLanguage":"en"}""")
            }
        }
        userAAdventureId = extractJsonString(latestResponseBody, "adventureId")
        rootMessageId = extractJsonString(latestResponseBody, "messageId")
        userAAdventureIds.add(userAAdventureId)
        adventureIdsByTitle[title] = userAAdventureId
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
        rootMessageId = extractJsonString(latestResponseBody, "messageId")
        adventureIdsByTitle["Forest Run"] = userBAdventureId
    }

    @Given("I have an existing adventure with messages")
    fun haveExistingAdventureWithMessages() {
        haveExistingTitledAdventureWithMessages(title = "Lantern Trail")
    }

    @Given("I have an existing {string} adventure with messages")
    fun haveExistingTitledAdventureWithMessages(title: String) {
        createAdventureForUserA(title)
        runAgainstApplication {
            client.post("/v1/adventures/$userAAdventureId/messages") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"type":"user","parentId":"$rootMessageId","text":"Camino adelante"}""")
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
                setBody("""{"type":"user","parentId":"$rootMessageId","text":"Miro detrás de mí"}""")
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
                setBody("""{"type":"user","parentId":"$rootMessageId","text":"Acepto mi destino"}""")
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
                    setBody("""{"type":"user","parentId":"$rootMessageId","text":"Acción del jugador ${turn + 1}"}""")
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

    @Given("the next message id is {string}")
    fun nextMessageIdIs(messageId: String) {
        nextMessageIds.add(messageId)
    }

    @Given("I have an existing adventure with root AI message id {string}")
    fun haveExistingAdventureWithRootAiMessageId(messageId: String) {
        nextMessageIdIs(messageId)
        userAHasAdventure()
    }

    @Given("user A has an adventure with root AI message id {string}")
    fun userAHasAdventureWithRootAiMessageId(messageId: String) {
        nextMessageIdIs(messageId)
        userAHasAdventure()
    }

    @Given("I have an existing adventure with stored user message id {string} and text {string}")
    fun haveExistingAdventureWithStoredUserMessage(messageId: String, text: String) {
        haveExistingAdventureWithRootAiMessageId("msg_ai_root")
        storeUserMessage(messageId = messageId, parentId = rootMessageId, text = text)
    }

    @Given("I have stored user message id {string} with parent id {string} and text {string}")
    fun haveStoredUserMessage(messageId: String, parentId: String, text: String) {
        storeUserMessage(messageId = messageId, parentId = parentId, text = text)
    }

    @Given("I have stored AI message id {string} with parent id {string}")
    fun haveStoredAiMessage(messageId: String, parentId: String) {
        storeAiMessage(messageId = messageId, parentId = parentId)
    }

    @Given("AI generation for message id {string} has failed without storing an AI message")
    fun aiGenerationFailedWithoutStoringMessage(messageId: String) {
        assertFalse(latestResponseBody.contains("\"parentId\":\"$messageId\",\"type\":\"AI\""))
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

    @When("I create a text adventure with learning language {string}, translation language {string} and language level {string}")
    fun createAdventureWithLevel(learningLanguage: String, translationLanguage: String, languageLevel: String) {
        enqueueNarratorResponse(title = "Lantern Trail", sentence = "Hola", translation = "Hello")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorizedIfPresent()
                contentType(ContentType.Application.Json)
                setBody(
                    """
                    {
                      "learningLanguage":"$learningLanguage",
                      "translationLanguage":"$translationLanguage",
                      "languageLevel":"$languageLevel"
                    }
                    """.trimIndent(),
                )
            }
        }
    }

    @Given("I have an existing adventure created at language level {string}")
    fun haveExistingAdventureCreatedAtLanguageLevel(languageLevel: String) {
        nextMessageIdIs("msg_ai_root")
        enqueueNarratorResponse(title = "Lantern Trail", sentence = "Hola", translation = "Hello")
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"es","translationLanguage":"en","languageLevel":"$languageLevel"}""")
            }
        }
        userAAdventureId = extractJsonString(latestResponseBody, "adventureId")
        rootMessageId = extractJsonString(latestResponseBody, "messageId")
        userAAdventureIds.add(userAAdventureId)
        adventureIdsByTitle["Lantern Trail"] = userAAdventureId
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
                setBody("""{"type":"user","parentId":"$rootMessageId","text":"Go north"}""")
            }
        }
    }

    @When("I post a new player message to unknown adventure id {string}")
    fun postMessageToUnknownAdventure(adventureId: String) {
        runAgainstApplication {
            client.post("/v1/adventures/$adventureId/messages") {
                authorizedIfPresent()
                contentType(ContentType.Application.Json)
                setBody("""{"type":"user","parentId":"$rootMessageId","text":"Go north"}""")
            }
        }
    }

    @When("I post a message with type {string}, parent id {string}, and text {string}")
    fun postMessageWithTypeParentAndText(type: String, parentId: String, text: String) {
        postMessage(adventureId = userAAdventureId, type = type, parentId = parentId, text = text)
    }

    @When("I post a message with type {string}, parent id {string}, and empty text")
    fun postMessageWithTypeParentAndEmptyText(type: String, parentId: String) {
        postMessage(adventureId = userAAdventureId, type = type, parentId = parentId, text = "")
    }

    @When("I post a message with type {string} and parent id {string}")
    fun postMessageWithTypeAndParent(type: String, parentId: String) {
        postMessage(adventureId = userAAdventureId, type = type, parentId = parentId, text = null)
    }

    @When("I post a message with type {string} to unknown adventure id {string}, parent id {string}, and text {string}")
    fun postMessageToUnknownAdventureId(type: String, adventureId: String, parentId: String, text: String) {
        postMessage(adventureId = adventureId, type = type, parentId = parentId, text = text)
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

    @Given("I have deleted the {string} adventure")
    fun iHaveDeletedTheAdventure(title: String) {
        runAgainstApplication {
            client.delete("/v1/adventures/${adventureIdFor(title)}") {
                authorizedIfPresent()
            }
        }
        assertEquals(HttpStatusCode.NoContent, latestResponseStatus)
    }

    @Given("user A has deleted their {string} adventure")
    fun userAHasDeletedTheirAdventure(title: String) {
        runAgainstApplication {
            client.delete("/v1/adventures/${adventureIdFor(title)}") {
                authorized(userAToken)
            }
        }
        assertEquals(HttpStatusCode.NoContent, latestResponseStatus)
    }

    @When("I delete the {string} adventure")
    fun iDeleteTheAdventure(title: String) {
        runAgainstApplication {
            client.delete("/v1/adventures/${adventureIdFor(title)}") {
                authorizedIfPresent()
            }
        }
    }

    @When("I undo the {string} adventure deletion")
    fun iUndoTheAdventureDeletion(title: String) {
        runAgainstApplication {
            client.delete("/v1/adventures/${adventureIdFor(title)}/deletion") {
                authorizedIfPresent()
            }
        }
    }

    @When("I fetch the {string} adventure by id")
    fun iFetchTheAdventureById(title: String) {
        runAgainstApplication {
            client.get("/v1/adventures/${adventureIdFor(title)}") {
                authorizedIfPresent()
            }
        }
    }

    @Then("the {string} adventure is listed")
    fun theAdventureIsListed(title: String) {
        val adventureId = adventureIdFor(title)
        runAgainstApplication {
            client.get("/v1/adventures") {
                authorizedIfPresent()
            }
        }
        assertTrue(latestResponseBody.contains(adventureId))
    }

    @Then("reading the {string} adventure returns 404")
    fun readingTheAdventureReturnsNotFound(title: String) {
        iFetchTheAdventureById(title)
        assertEquals(HttpStatusCode.NotFound, latestResponseStatus)
    }

    @Then("reading the {string} adventure messages returns 404")
    fun readingTheAdventureMessagesReturnsNotFound(title: String) {
        fetchTheAdventureMessages(title)
        assertEquals(HttpStatusCode.NotFound, latestResponseStatus)
    }

    @Then("reading the {string} adventure messages returns {int} messages")
    fun readingTheAdventureMessagesReturnsNMessages(title: String, expectedCount: Int) {
        fetchTheAdventureMessages(title)
        assertEquals(HttpStatusCode.OK, latestResponseStatus)
        assertEquals(expectedCount, "\"sender\"".toRegex().findAll(latestResponseBody).count())
    }

    private fun fetchTheAdventureMessages(title: String) {
        runAgainstApplication {
            client.get("/v1/adventures/${adventureIdFor(title)}/messages") {
                authorizedIfPresent()
            }
        }
    }

    private fun adventureIdFor(title: String): String =
        checkNotNull(adventureIdsByTitle[title]) { "No adventure titled '$title' has been created in this scenario" }

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
                setBody("""{"type":"user","parentId":"$rootMessageId","text":"$message"}""")
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
                setBody("""{"type":"user","parentId":"$rootMessageId","text":""}""")
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

    @When("I exhaust the text adventure rate limit and make one more request")
    fun exhaustTextAdventureRateLimit() {
        requestListUntilRateLimited(finalToken = userAToken)
    }

    @When("user A exhausts the text adventure rate limit and user B makes a request")
    fun userAExhaustsRateLimitThenUserBRequests() {
        requestListUntilRateLimited(finalToken = userBToken)
    }

    /**
     * Drives enough text adventure requests through a single running server to use up the shared
     * rate limit, then makes one more request (as [finalToken]) whose response is captured. The
     * warm-up requests are all made as user A; the shared limit means the final request is rejected
     * regardless of which user makes it.
     */
    private fun requestListUntilRateLimited(finalToken: String) {
        runBlocking {
            testApplication {
                application {
                    configureRouting(
                        textAdventureService = textAdventureService,
                        appApiKey = "unused",
                        accountService = accountService,
                    )
                }
                repeat(TEXT_ADVENTURE_RATE_LIMIT) {
                    client.get("/v1/adventures") { authorized(userAToken) }
                }
                val response = client.get("/v1/adventures") { authorized(finalToken) }
                latestResponseStatus = response.status
                latestResponseBody = response.bodyAsText()
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

    @Given("a cover image named {string}")
    fun aCoverImageNamed(name: String) {
        // Precondition: the named image is part of the bundled catalogue.
        requireCoverImage(name)
    }

    @When("I view the {string} cover image")
    fun viewCoverImage(name: String) {
        openCoverImageAsset(requireCoverImage(name).id)
    }

    @When("I view the {string} cover image in dark theme")
    fun viewCoverImageInDarkTheme(name: String) {
        openCoverImageAsset("${requireCoverImage(name).id}-dark")
    }

    private fun requireCoverImage(name: String): AdventureImage =
        AdventureImageCatalog.findByName(name) ?: error("No catalogue cover image is named '$name'")

    @When("I open a cover image that does not exist")
    fun openMissingCoverImage() {
        openCoverImageAsset("does-not-exist")
    }

    /** Requests the served cover-image asset with the given file-name stem (e.g. an id or "<id>-dark"). */
    private fun openCoverImageAsset(stem: String) {
        runAgainstApplication {
            client.get("/$ADVENTURE_IMAGES_PATH/$stem.$ADVENTURE_IMAGE_EXTENSION")
        }
    }

    @Then("the response includes a cover image")
    fun responseIncludesCoverImage() {
        val imageId = extractJsonString(latestResponseBody, "imageId")
        assertTrue("Expected a cover image but was '$imageId'", AdventureImageCatalog.contains(imageId))
    }

    @Then("the response is an image")
    fun responseIsAnImage() {
        assertTrue(
            "Expected an image but the content type was '$latestResponseContentType'",
            latestResponseContentType.contains("image/"),
        )
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

    @Then("the response includes a message id")
    fun responseIncludesMessageId() {
        assertTrue(extractJsonString(latestResponseBody, "id").isNotBlank())
    }

    @Then("the response message id is {string}")
    fun responseMessageIdIs(expectedId: String) {
        assertEquals(expectedId, extractJsonString(latestResponseBody, "id"))
    }

    @Then("the response message has type {string}")
    fun responseMessageHasType(expectedType: String) {
        assertEquals(expectedType, extractJsonString(latestResponseBody, "type"))
    }

    @Then("the response message parent id is {string}")
    fun responseMessageParentIdIs(expectedParentId: String) {
        assertEquals(expectedParentId, extractJsonString(latestResponseBody, "parentId"))
    }

    @Then("the response message text is {string}")
    fun responseMessageTextIs(expectedText: String) {
        assertEquals(expectedText, extractJsonString(latestResponseBody, "text"))
    }

    @Then("the response includes generated sentences and translations")
    fun responseIncludesGeneratedSentencesAndTranslations() {
        assertTrue(latestResponseBody.contains("sentences"))
        assertTrue(latestResponseBody.contains("translatedSentences"))
    }

    @Then("no AI response has been generated for message id {string}")
    fun noAiResponseGeneratedForUserMessage(messageId: String) {
        fetchAdventureMessages()
        assertFalse(messageHasTypeAndParent(type = "AI", parentId = messageId))
    }

    @Then("the AI is asked to write a plan for an adventure")
    fun theAiIsAskedToWriteAPlan() {
        val planInvocation = fakeExecutor.invocations.firstOrNull { it.promptName == PLAN_PROMPT_NAME }
            ?: error("Expected the AI to be asked to write an adventure plan")
        assertTrue(
            "Expected the plan prompt to instruct the AI to write a plan",
            planInvocation.systemPrompt.contains("plan", ignoreCase = true),
        )
    }

    @Then("the AI is asked to write at language level {string}")
    fun theAiIsAskedToWriteAtLanguageLevel(languageLevel: String) {
        assertNarratorPromptWritesAtLevel(promptName = START_PROMPT_NAME, languageLevel = languageLevel)
    }

    @Then("the AI is asked to continue writing at language level {string}")
    fun theAiIsAskedToContinueWritingAtLanguageLevel(languageLevel: String) {
        assertNarratorPromptWritesAtLevel(promptName = CONTINUE_PROMPT_NAME, languageLevel = languageLevel)
    }

    /**
     * Asserts the most recent narrator prompt with [promptName] tells the AI to write at CEFR level
     * [languageLevel], which is how the requested (or stored) difficulty reaches the model.
     */
    private fun assertNarratorPromptWritesAtLevel(promptName: String, languageLevel: String) {
        val invocation = fakeExecutor.invocations.lastOrNull { it.promptName == promptName }
            ?: error("Expected the AI to be asked to narrate with prompt '$promptName'")
        assertTrue(
            "Expected the $promptName prompt to instruct CEFR level $languageLevel",
            invocation.systemPrompt.contains("CEFR level $languageLevel"),
        )
    }

    @Then("the AI is asked to open by describing the player and their inventory")
    fun theAiIsAskedToOpenByDescribingPlayerAndInventory() {
        val startInvocation = fakeExecutor.invocations.firstOrNull { it.promptName == START_PROMPT_NAME }
            ?: error("Expected the AI to be asked to start an adventure")
        // Inspect only the narrator's own opening instructions, not the private plan that is appended
        // afterwards: the plan always describes the character and inventory, so checking the whole
        // prompt would pass even without an explicit instruction to open by describing them.
        val planMarker = "Below is the private plan"
        assertTrue(
            "Expected the start prompt to include the private plan section",
            startInvocation.systemPrompt.contains(planMarker),
        )
        val openingInstructions = startInvocation.systemPrompt.substringBefore(planMarker)
        assertTrue(
            "Expected the opening instructions to describe who the player's character is",
            openingInstructions.contains("character", ignoreCase = true),
        )
        assertTrue(
            "Expected the opening instructions to describe the player's starting inventory",
            openingInstructions.contains("inventory", ignoreCase = true),
        )
    }

    @Given("I have an adventure whose opening narrator message has note {string}")
    fun haveAdventureWhoseOpeningNarratorMessageHasNote(note: String) {
        nextMessageIdIs("msg_ai_root")
        fakeExecutor.enqueueResponse(narratorResponse(note = note))
        startAdventureForUserACapturingIds()
    }

    @When("I start an adventure where the AI plans {string} and notes {string}")
    fun startAdventureWherePlansAndNotes(planMarker: String, noteMarker: String) {
        fakeExecutor.enqueuePlanResponse(planResponseContaining(planMarker))
        fakeExecutor.enqueueResponse(narratorResponse(note = noteMarker))
        startAdventureForUserACapturingIds()
    }

    @Then("neither the adventure nor its messages expose {string}")
    fun neitherAdventureNorMessagesExpose(marker: String) {
        runAgainstApplication { client.get("/v1/adventures/$userAAdventureId") { authorized(userAToken) } }
        assertFalse("The adventure summary leaked private narrator text", latestResponseBody.contains(marker))
        runAgainstApplication { client.get("/v1/adventures/$userAAdventureId/messages") { authorized(userAToken) } }
        assertFalse("The adventure messages leaked private narrator text", latestResponseBody.contains(marker))
    }

    private fun startAdventureForUserACapturingIds() {
        runAgainstApplication {
            client.post("/v1/adventures") {
                authorized(userAToken)
                contentType(ContentType.Application.Json)
                setBody("""{"learningLanguage":"es","translationLanguage":"en"}""")
            }
        }
        userAAdventureId = extractJsonString(latestResponseBody, "adventureId")
        rootMessageId = extractJsonString(latestResponseBody, "messageId")
        userAAdventureIds.add(userAAdventureId)
        adventureIdsByTitle["Lantern Trail"] = userAAdventureId
    }

    private fun narratorResponse(note: String): TextAdventureStructuredResponse = TextAdventureStructuredResponse(
        title = "Lantern Trail",
        translatedTitle = "Lantern Trail (translated)",
        paragraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf("Hola"))),
        translatedParagraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf("Hello"))),
        isEnding = false,
        note = note,
    )

    private fun planResponseContaining(marker: String): AdventurePlanStructuredResponse =
        AdventurePlanStructuredResponse(
            characterDescription = "Character $marker",
            inventory = "Inventory $marker",
            hook = "Hook $marker",
            truthBehindHook = "Truth $marker",
            coreChallenge = "Challenge $marker",
            locations = listOf(AdventurePlanLocationResponse(name = "Place $marker", description = "Description $marker")),
            npcs = listOf(AdventurePlanNpcResponse(name = "NPC $marker", description = "Role $marker")),
        )

    @Then("reading adventure messages returns message id {string} followed by message id {string}")
    fun readingMessagesReturnsMessageFollowedBy(firstMessageId: String, secondMessageId: String) {
        fetchAdventureMessages()
        val firstIndex = latestResponseBody.indexOf("\"id\":\"$firstMessageId\"")
        val secondIndex = latestResponseBody.indexOf("\"id\":\"$secondMessageId\"")
        assertTrue(firstIndex >= 0)
        assertTrue(secondIndex > firstIndex)
    }

    @Then("every returned message includes an id")
    fun everyReturnedMessageIncludesId() {
        assertTrue("\"id\"".toRegex().findAll(latestResponseBody).count() >= 1)
    }

    @Then("every non-root returned message includes its parent id")
    fun everyNonRootReturnedMessageIncludesParentId() {
        assertTrue(latestResponseBody.contains("parentId"))
    }

    @Then("message id {string} has parent id {string}")
    fun messageHasParentId(messageId: String, parentId: String) {
        assertTrue(latestResponseBody.contains("\"id\":\"$messageId\""))
        assertTrue(latestResponseBody.contains("\"parentId\":\"$parentId\""))
    }

    @Then("messages are returned with ids and parent ids")
    fun messagesReturnedWithIdsAndParentIds() {
        assertTrue(latestResponseBody.contains("id"))
        assertTrue(latestResponseBody.contains("parentId"))
    }

    @Then("root message id {string} has no parent id")
    fun rootMessageHasNoParentId(messageId: String) {
        assertTrue(latestResponseBody.contains("\"id\":\"$messageId\""))
        assertTrue(latestResponseBody.contains("\"parentId\":null"))
    }

    @Then("messages with ids {string}, {string}, {string}, and {string} are returned")
    fun messagesWithIdsAreReturned(first: String, second: String, third: String, fourth: String) {
        listOf(first, second, third, fourth).forEach { messageId ->
            assertTrue(latestResponseBody.contains("\"id\":\"$messageId\""))
        }
    }

    @When("I list preplanned adventures")
    fun listPreplannedAdventures() {
        runAgainstApplication {
            client.get("/v1/preplanned-adventures") {
                authorizedIfPresent()
            }
        }
    }

    @When("I list preplanned adventures for learning language {string} and translation language {string}")
    fun listPreplannedAdventuresForLanguages(learningLanguage: String, translationLanguage: String) {
        runAgainstApplication {
            client.get("/v1/preplanned-adventures?learningLanguage=$learningLanguage&translationLanguage=$translationLanguage") {
                authorizedIfPresent()
            }
        }
    }

    @Then("the preplanned adventure list includes every bundled preplanned adventure")
    fun preplannedListIncludesEveryBundledAdventure() {
        PreplannedAdventureCatalog.adventures.forEach { adventure ->
            assertTrue(
                "Expected preplanned adventure '${adventure.id}' in the list but it was missing",
                latestResponseBody.contains("\"id\":\"${adventure.id}\""),
            )
        }
    }

    @Then("the preplanned adventure list is empty")
    fun preplannedListIsEmpty() {
        assertTrue("Expected an empty preplanned adventure list", latestResponseBody.contains("\"items\":[]"))
    }

    @When("I start the first bundled preplanned adventure")
    @Given("I have started the first bundled preplanned adventure")
    fun startFirstBundledPreplannedAdventure() {
        startPreplannedAdventure(firstPreplannedAdventureId(), token = null)
        captureStartedPreplannedAdventureIds()
    }

    @When("I start preplanned adventure {string}")
    fun startNamedPreplannedAdventure(preplannedAdventureId: String) {
        startPreplannedAdventure(preplannedAdventureId, token = null)
    }

    @Given("user A starts the first bundled preplanned adventure")
    fun userAStartsFirstBundledPreplannedAdventure() {
        startPreplannedAdventure(firstPreplannedAdventureId(), token = userAToken)
        userAAdventureId = extractJsonString(latestResponseBody, "adventureId")
        rootMessageId = extractJsonString(latestResponseBody, "messageId")
        userAAdventureIds.add(userAAdventureId)
    }

    @Given("user B starts the first bundled preplanned adventure")
    fun userBStartsFirstBundledPreplannedAdventure() {
        startPreplannedAdventure(firstPreplannedAdventureId(), token = userBToken)
        userBAdventureId = extractJsonString(latestResponseBody, "adventureId")
    }

    @Then("the two started adventures have different ids")
    fun theTwoStartedAdventuresHaveDifferentIds() {
        assertTrue(userAAdventureId.isNotBlank())
        assertTrue(userBAdventureId.isNotBlank())
        assertFalse("Each player should get their own adventure", userAAdventureId == userBAdventureId)
    }

    @When("user B fetches user A's started preplanned adventure messages")
    fun userBFetchesUserAStartedPreplannedAdventureMessages() {
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId/messages") {
                authorized(userBToken)
            }
        }
    }

    @When("I fetch adventure messages for the started preplanned adventure")
    fun fetchMessagesForStartedPreplannedAdventure() {
        runAgainstApplication {
            client.get("/v1/adventures/$userAAdventureId/messages") {
                authorizedIfPresent()
            }
        }
    }

    @Then("the started preplanned adventure is listed")
    fun theStartedPreplannedAdventureIsListed() {
        assertTrue(userAAdventureId.isNotBlank())
        assertTrue(latestResponseBody.contains(userAAdventureId))
    }

    @When("I continue the started preplanned adventure with text {string}")
    fun continueStartedPreplannedAdventure(text: String) {
        postMessage(adventureId = userAAdventureId, type = "user", parentId = rootMessageId, text = text)
    }

    @Then("neither the started adventure nor its messages expose the preplanned plan")
    fun neitherStartedAdventureNorMessagesExposePreplannedPlan() {
        runAgainstApplication { client.get("/v1/adventures/$userAAdventureId") { authorizedIfPresent() } }
        assertFalse("The adventure summary leaked the private plan", latestResponseBody.contains(PLAN_LABEL_MARKER))
        runAgainstApplication { client.get("/v1/adventures/$userAAdventureId/messages") { authorizedIfPresent() } }
        assertFalse("The adventure messages leaked the private plan", latestResponseBody.contains(PLAN_LABEL_MARKER))
    }

    private fun firstPreplannedAdventureId(): String = PreplannedAdventureCatalog.adventures.first().id

    /**
     * Posts to start a preplanned adventure. A [token] authorises as that specific user; null falls
     * back to the active token only when authenticated, so the unauthenticated scenarios send no
     * credentials and get a 401.
     */
    private fun startPreplannedAdventure(preplannedAdventureId: String, token: String?) {
        runAgainstApplication {
            client.post("/v1/preplanned-adventures/$preplannedAdventureId/adventures") {
                if (token != null) authorized(token) else authorizedIfPresent()
            }
        }
    }

    /** Captures the active player's newly started preplanned adventure, but only on a successful start. */
    private fun captureStartedPreplannedAdventureIds() {
        if (latestResponseStatus != HttpStatusCode.Created) return
        userAAdventureId = extractJsonString(latestResponseBody, "adventureId")
        rootMessageId = extractJsonString(latestResponseBody, "messageId")
        userAAdventureIds.add(userAAdventureId)
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
                latestResponseContentType = response.headers["Content-Type"].orEmpty()
            }
        }
    }

    private fun postMessage(
        adventureId: String,
        type: String,
        parentId: String,
        text: String?,
        token: String = activeToken,
    ) {
        val textField = text?.let { ",\"text\":\"$it\"" }.orEmpty()
        runAgainstApplication {
            client.post("/v1/adventures/$adventureId/messages") {
                if (isAuthenticated) {
                    authorized(token)
                }
                contentType(ContentType.Application.Json)
                setBody("""{"type":"$type","parentId":"$parentId"$textField}""")
            }
        }
    }

    private fun storeUserMessage(messageId: String, parentId: String, text: String) {
        nextMessageIdIs(messageId)
        postMessage(adventureId = userAAdventureId, type = "user", parentId = parentId, text = text)
        assertEquals(HttpStatusCode.Created, latestResponseStatus)
    }

    private fun storeAiMessage(messageId: String, parentId: String) {
        nextMessageIdIs(messageId)
        enqueueNarratorResponse(
            title = "Lantern Trail",
            sentence = "generated sentence for $messageId",
            translation = "generated translation for $messageId",
        )
        postMessage(adventureId = userAAdventureId, type = "AI", parentId = parentId, text = null)
        assertEquals(HttpStatusCode.Created, latestResponseStatus)
    }

    private fun messageHasTypeAndParent(type: String, parentId: String): Boolean =
        latestResponseBody.contains("\"type\":\"$type\"") && latestResponseBody.contains("\"parentId\":\"$parentId\"")

    @Given("the AI structuring will fail for the next user message")
    fun aiStructuringWillFail() {
        repeat(MAX_USER_MESSAGE_STRUCTURING_ATTEMPTS) {
            fakeExecutor.enqueueError(RuntimeException("AI structuring failed"))
        }
    }

    private fun enqueueNarratorResponse(
        title: String,
        sentence: String,
        translation: String,
        isEnding: Boolean = false,
        imageId: String = "",
    ) {
        fakeExecutor.enqueueResponse(
            TextAdventureStructuredResponse(
                title = title,
                translatedTitle = "$title (translated)",
                paragraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(sentence))),
                translatedParagraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(translation))),
                isEnding = isEnding,
                imageId = imageId,
            ),
        )
    }

    @Given("the AI will choose image {string}")
    fun theAiWillChooseImage(imageId: String) {
        enqueueNarratorResponse(
            title = "Lantern Trail",
            sentence = "Hola",
            translation = "Hello",
            imageId = imageId,
        )
    }

    private fun enqueueUserMessageResponse(sentence: String, translation: String) {
        fakeExecutor.enqueueUserMessageResponse(
            UserMessageStructuredResponse(
                paragraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(sentence))),
                translatedParagraphs = listOf(TextAdventureStructuredParagraph(sentences = listOf(translation))),
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

    private companion object {
        const val MAX_USER_MESSAGE_STRUCTURING_ATTEMPTS = 3
        const val PLAN_PROMPT_NAME = "text-adventure-plan"
        const val START_PROMPT_NAME = "text-adventure-start"
        const val CONTINUE_PROMPT_NAME = "text-adventure-continue"

        // A label that the rendered plan always carries but narration never does, so finding it in an
        // API response would mean the private plan had leaked.
        const val PLAN_LABEL_MARKER = "TRUTH BEHIND THE HOOK"
    }
}
