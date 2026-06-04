package input.comprehensible.data.account.sources.remote

import input.comprehensible.data.account.InvalidCredentialsException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAccountRemoteDataSourceTest {

    @Test
    fun `deleteAccount succeeds on 204 response`() = runTest {
        val source = buildDataSource(MockEngine { respond("", HttpStatusCode.NoContent) })
        source.deleteAccount("alice@example.com", "SecurePass123!")
    }

    @Test
    fun `deleteAccount throws InvalidCredentialsException on 401`() = runTest {
        val source = buildDataSource(MockEngine { respond("", HttpStatusCode.Unauthorized) })
        val result = runCatching { source.deleteAccount("alice@example.com", "wrong-password") }
        assertTrue(result.exceptionOrNull() is InvalidCredentialsException)
    }

    @Test
    fun `deleteAccount throws on non-success response`() = runTest {
        val source = buildDataSource(MockEngine { respond("", HttpStatusCode.InternalServerError) })
        val result = runCatching { source.deleteAccount("alice@example.com", "SecurePass123!") }
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `deleteAccount sends email and password in request body`() = runTest {
        var capturedBody = ""
        val source = buildDataSource(MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond("", HttpStatusCode.NoContent)
        })
        source.deleteAccount("alice@example.com", "SecurePass123!")
        assertTrue(capturedBody.contains("alice@example.com"))
        assertTrue(capturedBody.contains("SecurePass123!"))
    }

    @Test
    fun `deleteAccount sends request to correct endpoint`() = runTest {
        var capturedUrl = ""
        val source = buildDataSource(MockEngine { request ->
            capturedUrl = request.url.toString()
            respond("", HttpStatusCode.NoContent)
        })
        source.deleteAccount("alice@example.com", "SecurePass123!")
        assertEquals("https://test.example.com/v1/me", capturedUrl)
    }

    private fun buildDataSource(engine: MockEngine) = DefaultAccountRemoteDataSource(
        baseUrl = "https://test.example.com",
        apiKey = "test-api-key",
        httpClient = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        },
    )
}
