package input.comprehensible.data.textadventures.sources.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class DefaultTextAdventureRemoteDataSource(
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
        }
    },
) : TextAdventureRemoteDataSource {
    override suspend fun startAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ): TextAdventureRemoteResponse = httpClient.post("$BACKEND_BASE_URL/text-adventures/start") {
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
