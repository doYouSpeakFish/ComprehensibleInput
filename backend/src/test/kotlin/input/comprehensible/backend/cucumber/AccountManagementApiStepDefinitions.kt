package input.comprehensible.backend.cucumber

import input.comprehensible.backend.AccountService
import input.comprehensible.backend.configureRouting
import input.comprehensible.backend.connectDatabase
import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.testing.FakeTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.testing.PostgreSqlTestDatabase
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.Assert.assertEquals

class AccountManagementApiStepDefinitions {
    private lateinit var database: Database
    private lateinit var accountService: AccountService
    private var latestStatus: HttpStatusCode? = null
    private var latestBody: String = ""
    private var bearerToken: String = ""
    private var currentPassword: String = ""
    private val json = Json { ignoreUnknownKeys = true }

    @Before fun setUp() { database = connectDatabase(PostgreSqlTestDatabase.createConfig()); accountService = AccountService(database) }
    @After fun tearDown() { PostgreSqlTestDatabase.reset(database); TransactionManager.closeAndUnregister(database) }

    @Given("existing user {string} with password {string}")
    fun existingUser(email: String, password: String) {
        createUser(email, password)
        assertEquals(HttpStatusCode.OK, latestStatus)
    }

    @Given("I am signed in with email {string} and password {string}")
    fun signInGiven(email: String, password: String) {
        signIn(email, password)
        currentPassword = password
        if (latestStatus == HttpStatusCode.OK) bearerToken = json.decodeFromString<SignInResponse>(latestBody).accessToken
    }

    @When("I create user with email {string} and password {string}")
    fun createUser(email: String, password: String) = runCall {
        post("/v1/users") { contentType(ContentType.Application.Json); setBody(credentialsBody(email, password)) }
    }

    @When("I sign in with email {string} and password {string}")
    fun signIn(email: String, password: String) = runCall {
        post("/v1/auth/sessions") { contentType(ContentType.Application.Json); setBody(credentialsBody(email, password)) }
    }

    @When("I request me profile")
    fun getMe() = runCall { get("/v1/me") { header(HttpHeaders.Authorization, "Bearer $bearerToken") } }

    @When("I request me profile without authorization")
    fun getMeNoAuth() = runCall { get("/v1/me") }

    @When("I update me email to {string}")
    fun patchMe(email: String) = runCall {
        patch("/v1/me") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$email\",\"password\":\"$currentPassword\"}")
        }
    }

    @When("I delete me")
    fun deleteMe() = runCall {
        delete("/v1/me") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
            contentType(ContentType.Application.Json)
            setBody("{\"password\":\"$currentPassword\"}")
        }
    }

    @When("I sign out current session")
    fun signOutCurrent() = runCall { delete("/v1/auth/sessions/current") { header(HttpHeaders.Authorization, "Bearer $bearerToken") } }

    @When("I sign out current session with invalid token")
    fun signOutInvalid() = runCall { delete("/v1/auth/sessions/current") { header(HttpHeaders.Authorization, "Bearer bad") } }

    @Then("account API status should be {int}")
    fun statusShouldBe(status: Int) { assertEquals(HttpStatusCode.fromValue(status), latestStatus) }

    private fun runCall(block: suspend io.ktor.client.HttpClient.() -> io.ktor.client.statement.HttpResponse) {
        testApplication {
            application {
                configureRouting(
                    textAdventureService = TextAdventureGenerationService(
                        FakeTextAdventureStructuredPromptExecutor(),
                        DatabaseAdventureRepository(database),
                    ),
                    appApiKey = "test",
                    accountService = accountService,
                )
            }
            val response = client.block()
            latestStatus = response.status
            latestBody = response.bodyAsText()
        }
    }

    private fun credentialsBody(email: String, password: String) = "{\"email\":\"$email\",\"password\":\"$password\"}"
}

@Serializable
private data class SignInResponse(
    @SerialName("access_token")
    val accessToken: String,
)
