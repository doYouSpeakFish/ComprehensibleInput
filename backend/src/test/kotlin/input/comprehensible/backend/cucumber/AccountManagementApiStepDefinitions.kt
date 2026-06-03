package input.comprehensible.backend.cucumber

import input.comprehensible.backend.AccountService
import input.comprehensible.backend.configureRouting
import input.comprehensible.backend.connectDatabase
import input.comprehensible.backend.email.EmailDataSource
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
    private var currentEmail: String = ""
    private var currentPassword: String = ""
    private var nextVerificationCode: String = "123456"
    private var now: Long = 1_000_000L
    private val fakeEmailDataSource = FakeEmailDataSource()
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        database = connectDatabase(PostgreSqlTestDatabase.createConfig())
        accountService = AccountService(
            database = database,
            emailDataSource = fakeEmailDataSource,
            verificationCodeProvider = { nextVerificationCode },
            currentTimeMillisProvider = { now },
        )
    }
    @After fun tearDown() { PostgreSqlTestDatabase.reset(database); TransactionManager.closeAndUnregister(database) }

    @Given("existing user {string} with password {string}")
    fun existingUser(email: String, password: String) {
        createUser(email, password)
        assertEquals(HttpStatusCode.OK, latestStatus)
    }

    @Given("I am signed in with email {string} and password {string}")
    fun signInGiven(email: String, password: String) {
        signIn(email, password)
        currentEmail = email
        currentPassword = password
        if (latestStatus == HttpStatusCode.OK) bearerToken = json.decodeFromString<SignInResponse>(latestBody).accessToken
    }

    @When("I create user with email {string} and password {string}")
    fun createUser(email: String, password: String) = runCall {
        post("/v1/users") { contentType(ContentType.Application.Json); setBody(credentialsBody(email, password)) }
    }

    @Given("the next verification code will be {string}")
    fun nextVerificationCode(code: String) {
        nextVerificationCode = code
    }

    @Given("time advances by {int} minutes and {int} seconds")
    fun timeAdvancesBy(minutes: Int, seconds: Int) {
        now += ((minutes * 60L) + seconds) * 1000L
    }

    @When("I sign in with email {string} and password {string}")
    fun signIn(email: String, password: String) = runCall {
        post("/v1/auth/sessions") { contentType(ContentType.Application.Json); setBody(credentialsBody(email, password)) }
    }

    @When("I verify email {string} using code {string}")
    fun verifyEmail(email: String, code: String) = runCall {
        post("/v1/email-verifications") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$email\",\"code\":\"$code\"}")
        }
    }

    @When("I request a password reset code for {string}")
    fun requestPasswordResetCode(email: String) = runCall {
        post("/v1/password-reset-codes") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$email\"}")
        }
    }

    @When("I reset password for {string} to {string} using code {string}")
    fun resetPassword(email: String, password: String, code: String) = runCall {
        post("/v1/password-resets") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$email\",\"password\":\"$password\",\"code\":\"$code\"}")
        }
    }

    @When("I request a new email verification code for {string}")
    fun requestNewEmailVerificationCode(email: String) = runCall {
        post("/v1/email-verification-codes") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$email\"}")
        }
    }

    @When("I request a new email change current verification code")
    fun requestNewEmailChangeCurrentVerificationCode() = runCall {
        post("/v1/email-change-current-verification-codes") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }
    }

    @When("I request a new email change current verification code without authorization")
    fun requestNewEmailChangeCurrentVerificationCodeNoAuth() = runCall {
        post("/v1/email-change-current-verification-codes")
    }

    @When("I request a new email change new-email verification code")
    fun requestNewEmailChangeNewEmailVerificationCode() = runCall {
        post("/v1/email-change-new-verification-codes") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
        }
    }

    @When("I request a new email change new-email verification code without authorization")
    fun requestNewEmailChangeNewEmailVerificationCodeNoAuth() = runCall {
        post("/v1/email-change-new-verification-codes")
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
    @When("I update me email to {string} without authorization")
    fun patchMeNoAuth(email: String) = runCall {
        patch("/v1/me") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$email\",\"password\":\"$currentPassword\"}")
        }
    }


    @When("I verify pending email change to {string} using code {string}")
    fun verifyPendingEmailChange(email: String, code: String) = runCall {
        post("/v1/email-change-verifications") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$email\",\"code\":\"$code\"}")
        }
    }

    @When("I verify current email change using code {string}")
    fun verifyCurrentEmailChange(code: String) = runCall {
        post("/v1/email-change-current-verifications") {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
            contentType(ContentType.Application.Json)
            setBody("{\"code\":\"$code\"}")
        }
    }

    @Then("account profile email should be {string}")
    fun accountProfileEmailShouldBe(email: String) {
        val profile = json.decodeFromString<AccountProfileResponse>(latestBody)
        assertEquals(email, profile.email)
    }

    @When("I delete me")
    fun deleteMe() = runCall {
        delete("/v1/me") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$currentEmail\",\"password\":\"$currentPassword\"}")
        }
    }
    @When("I delete me with wrong password")
    fun deleteMeWithWrongPassword() = runCall {
        delete("/v1/me") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\":\"$currentEmail\",\"password\":\"wrongpassword\"}")
        }
    }
    @When("I attempt to delete me a second time")
    fun deleteMeSecondAttempt() = runTwoCalls(
        first = {
            delete("/v1/me") {
                contentType(ContentType.Application.Json)
                setBody("{\"email\":\"$currentEmail\",\"password\":\"$currentPassword\"}")
            }
        },
        second = {
            delete("/v1/me") {
                contentType(ContentType.Application.Json)
                setBody("{\"email\":\"$currentEmail\",\"password\":\"$currentPassword\"}")
            }
        },
    )
    @When("I attempt to delete me a second time with malformed body")
    fun deleteMeSecondAttemptMalformedBody() = runTwoCalls(
        first = {
            delete("/v1/me") {
                contentType(ContentType.Application.Json)
                setBody("not-json")
            }
        },
        second = {
            delete("/v1/me") {
                contentType(ContentType.Application.Json)
                setBody("not-json")
            }
        },
    )
    @When("I attempt to delete me a second time with malformed body and forwarded IP")
    fun deleteMeSecondAttemptMalformedBodyWithForwardedIp() = runTwoCalls(
        first = {
            delete("/v1/me") {
                header("X-Forwarded-For", "192.168.1.1")
                contentType(ContentType.Application.Json)
                setBody("not-json")
            }
        },
        second = {
            delete("/v1/me") {
                header("X-Forwarded-For", "192.168.1.1")
                contentType(ContentType.Application.Json)
                setBody("not-json")
            }
        },
    )
    @When("I attempt to verify email a second time with email in query parameter")
    fun verifyEmailSecondAttemptWithQueryParam() = runTwoCalls(
        first = {
            post("/v1/email-verifications?email=$currentEmail") {
                contentType(ContentType.Application.Json)
                setBody("{\"email\":\"$currentEmail\",\"code\":\"$nextVerificationCode\"}")
            }
        },
        second = {
            post("/v1/email-verifications?email=$currentEmail") {
                contentType(ContentType.Application.Json)
                setBody("{\"email\":\"$currentEmail\",\"code\":\"$nextVerificationCode\"}")
            }
        },
    )
    @When("I attempt to verify email a second time with forwarded IP")
    fun verifyEmailSecondAttemptWithForwardedIp() = runTwoCalls(
        first = {
            post("/v1/email-verifications") {
                header("X-Forwarded-For", "192.168.1.1")
                contentType(ContentType.Application.Json)
                setBody("{\"email\":\"$currentEmail\",\"code\":\"$nextVerificationCode\"}")
            }
        },
        second = {
            post("/v1/email-verifications") {
                header("X-Forwarded-For", "192.168.1.1")
                contentType(ContentType.Application.Json)
                setBody("{\"email\":\"$currentEmail\",\"code\":\"$nextVerificationCode\"}")
            }
        },
    )

    @When("I sign out current session")
    fun signOutCurrent() = runCall { delete("/v1/auth/sessions/current") { header(HttpHeaders.Authorization, "Bearer $bearerToken") } }

    @When("I sign out current session with invalid token")
    fun signOutInvalid() = runCall { delete("/v1/auth/sessions/current") { header(HttpHeaders.Authorization, "Bearer bad") } }
    @When("I sign out current session without token")
    fun signOutNoAuth() = runCall { delete("/v1/auth/sessions/current") }

    @Then("account API status should be {int}")
    fun statusShouldBe(status: Int) { assertEquals(HttpStatusCode.fromValue(status), latestStatus) }

    @Then("an email should be sent to {string} containing {string}")
    fun emailShouldBeSent(to: String, containsText: String) {
        val sent = fakeEmailDataSource.sentEmails.lastOrNull { it.to == to }
        requireNotNull(sent) { "No email sent to $to" }
        org.junit.Assert.assertTrue(sent.subject.contains(containsText) || sent.textBody.contains(containsText))
    }

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

    private fun runTwoCalls(
        first: suspend io.ktor.client.HttpClient.() -> io.ktor.client.statement.HttpResponse,
        second: suspend io.ktor.client.HttpClient.() -> io.ktor.client.statement.HttpResponse,
    ) {
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
            client.first()
            val response = client.second()
            latestStatus = response.status
            latestBody = response.bodyAsText()
        }
    }

    private fun credentialsBody(email: String, password: String) = "{\"email\":\"$email\",\"password\":\"$password\"}"
}

private class FakeEmailDataSource : EmailDataSource {
    val sentEmails = mutableListOf<SentEmail>()
    override suspend fun sendEmail(to: String, subject: String, textBody: String) {
        sentEmails += SentEmail(to, subject, textBody)
    }
}

private data class SentEmail(val to: String, val subject: String, val textBody: String)

@Serializable
private data class SignInResponse(
    @SerialName("access_token")
    val accessToken: String,
)

@Serializable
private data class AccountProfileResponse(
    val email: String,
)
