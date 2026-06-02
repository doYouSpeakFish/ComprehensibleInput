package input.comprehensible.data.textadventures.sources.remote

import input.comprehensible.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
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
        install(Logging)
    },
) : TextAdventureRemoteDataSource {
    override suspend fun startAdventure(
        learningLanguage: String,
        translationLanguage: String,
        sessionToken: String,
    ): TextAdventureRemoteResponse = httpClient.post("${BuildConfig.BACKEND_BASE_URL}/v1/adventures") {
        header("Authorization", "Bearer $sessionToken")
        contentType(ContentType.Application.Json)
        setBody(StartAdventureRequest(learningLanguage = learningLanguage, translationLanguage = translationLanguage))
    }.body()

    override suspend fun createUserMessage(
        adventureId: String,
        parentMessageId: String,
        text: String,
        sessionToken: String,
    ): TextAdventureMessageRemoteResponse = httpClient.post(
        "${BuildConfig.BACKEND_BASE_URL}/v1/adventures/$adventureId/messages"
    ) {
        header("Authorization", "Bearer $sessionToken")
        contentType(ContentType.Application.Json)
        setBody(PostMessageRequest(type = "user", parentId = parentMessageId, text = text))
    }.body()

    override suspend fun createAiMessage(
        adventureId: String,
        parentMessageId: String,
        sessionToken: String,
    ): TextAdventureMessageRemoteResponse = httpClient.post(
        "${BuildConfig.BACKEND_BASE_URL}/v1/adventures/$adventureId/messages"
    ) {
        header("Authorization", "Bearer $sessionToken")
        contentType(ContentType.Application.Json)
        setBody(PostMessageRequest(type = "AI", parentId = parentMessageId))
    }.body()
}

@Serializable
private data class StartAdventureRequest(
    val learningLanguage: String,
    val translationLanguage: String,
)

@Serializable
private data class PostMessageRequest(
    val type: String,
    val parentId: String,
    val text: String? = null,
)
