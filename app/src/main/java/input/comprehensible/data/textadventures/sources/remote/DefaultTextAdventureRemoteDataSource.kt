package input.comprehensible.data.textadventures.sources.remote

import input.comprehensible.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIMEOUT_MILLIS = 60_000L

class DefaultTextAdventureRemoteDataSource(
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MILLIS
        }
    },
) : TextAdventureRemoteDataSource {
    override suspend fun startAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse = httpClient.post("$BACKEND_BASE_URL/text-adventures/start") {
        header("X-Api-Key", BuildConfig.BACKEND_API_KEY)
        contentType(ContentType.Application.Json)
        setBody(
            StartTextAdventureRequest(
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
            )
        )
    }.body()

    override suspend fun respondToUser(
        adventureId: String,
        learningLanguage: String,
        translationsLanguage: String,
        userMessage: String,
        history: List<TextAdventureHistoryMessage>,
    ): TextAdventureRemoteResponse = httpClient.post("$BACKEND_BASE_URL/text-adventures/respond") {
        header("X-Api-Key", BuildConfig.BACKEND_API_KEY)
        contentType(ContentType.Application.Json)
        setBody(
            ContinueTextAdventureRequest(
                adventureId = adventureId,
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
                userMessage = userMessage,
                history = history,
            )
        )
    }.body()

    private companion object {
        const val BACKEND_BASE_URL =
            "https://comprehensibleinput-844851864443.europe-west1.run.app"
    }
}
