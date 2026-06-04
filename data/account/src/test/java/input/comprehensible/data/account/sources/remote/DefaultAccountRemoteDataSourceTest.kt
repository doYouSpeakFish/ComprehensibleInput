package input.comprehensible.data.account.sources.remote

import input.comprehensible.data.account.InvalidCredentialsException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAccountRemoteDataSourceTest {

    @Test
    fun `deleteAccount succeeds when server accepts the request`() = runTest {
        // GIVEN the server accepts the deletion
        val source = buildDataSource(MockEngine { respond("", HttpStatusCode.NoContent) })

        // WHEN deleteAccount is called
        val result = runCatching { source.deleteAccount("alice@example.com", "SecurePass123!") }

        // THEN it succeeds
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteAccount signals invalid credentials when server rejects the password`() = runTest {
        // GIVEN the server rejects the supplied credentials
        val source = buildDataSource(MockEngine { respond("", HttpStatusCode.Unauthorized) })

        // WHEN deleteAccount is called
        val result = runCatching { source.deleteAccount("alice@example.com", "wrong-password") }

        // THEN it signals that the credentials were invalid
        assertTrue(result.exceptionOrNull() is InvalidCredentialsException)
    }

    @Test
    fun `deleteAccount fails when the server reports an error`() = runTest {
        // GIVEN the server returns an error
        val source = buildDataSource(MockEngine { respond("", HttpStatusCode.InternalServerError) })

        // WHEN deleteAccount is called
        val result = runCatching { source.deleteAccount("alice@example.com", "SecurePass123!") }

        // THEN the call fails
        assertTrue(result.isFailure)
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
