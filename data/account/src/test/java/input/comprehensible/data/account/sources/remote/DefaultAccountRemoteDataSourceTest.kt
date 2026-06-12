package input.comprehensible.data.account.sources.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultAccountRemoteDataSourceTest {

    @Test
    fun `sends the app UI language as the Accept-Language header`() = runTest {
        // GIVEN a data source told the app is currently displayed in German
        val sentLanguages = mutableListOf<String?>()
        val dataSource = dataSource(languageTagProvider = { "de" }, capture = sentLanguages)

        // WHEN it makes a request that can trigger a backend email
        dataSource.createAccount(email = "alice@example.com", password = "SecurePass123!")

        // THEN the request carries the app language so the backend can localise the email
        assertEquals("de", sentLanguages.single())
    }

    @Test
    fun `reads the language for each request so a locale change is reflected`() = runTest {
        // GIVEN a data source whose reported language changes between requests
        val sentLanguages = mutableListOf<String?>()
        var language = "fr"
        val dataSource = dataSource(languageTagProvider = { language }, capture = sentLanguages)

        // WHEN two requests are made either side of a language change
        dataSource.requestPasswordResetCode(email = "alice@example.com")
        language = "pt"
        dataSource.requestPasswordResetCode(email = "alice@example.com")

        // THEN each request reports the language current at the time it was sent
        assertEquals(listOf("fr", "pt"), sentLanguages)
    }

    private fun dataSource(
        languageTagProvider: () -> String,
        capture: MutableList<String?>,
    ) = DefaultAccountRemoteDataSource(
        baseUrl = "https://example.test",
        apiKey = "test-key",
        languageTagProvider = languageTagProvider,
        httpClient = HttpClient(
            MockEngine { request ->
                capture += request.headers[HttpHeaders.AcceptLanguage]
                respond("", HttpStatusCode.OK)
            },
        ) {
            install(ContentNegotiation) { json() }
        },
    )
}
