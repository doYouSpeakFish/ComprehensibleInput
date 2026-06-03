package input.comprehensible.data.textadventures.sources.remote

import input.comprehensible.BuildConfig
import input.comprehensible.data.account.SessionProvider
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
import kotlinx.serialization.json.Json

private const val TIMEOUT_MILLIS = 60_000L

class DefaultTextAdventureRemoteDataSource(
    private val sessionProvider: SessionProvider = SessionProvider(),
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
    ): TextAdventureRemoteResponse {
        val token = requireToken()
        return httpClient.post("${BuildConfig.BACKEND_BASE_URL}/v1/adventures") {
            header("X-Api-Key", BuildConfig.BACKEND_API_KEY)
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(
                StartTextAdventureV1Request(
                    learningLanguage = learningLanguage,
                    translationLanguage = translationLanguage,
                )
            )
        }.body()
    }

    override suspend fun postUserMessage(
        adventureId: String,
        parentId: String,
        text: String,
    ): TextAdventureMessageRemoteResponse {
        val token = requireToken()
        return httpClient.post(
            "${BuildConfig.BACKEND_BASE_URL}/v1/adventures/$adventureId/messages"
        ) {
            header("X-Api-Key", BuildConfig.BACKEND_API_KEY)
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(
                PostTextAdventureMessageV1Request(
                    type = MESSAGE_TYPE_USER,
                    parentId = parentId,
                    text = text,
                )
            )
        }.body()
    }

    override suspend fun postAiMessage(
        adventureId: String,
        parentId: String,
    ): TextAdventureMessageRemoteResponse {
        val token = requireToken()
        return httpClient.post(
            "${BuildConfig.BACKEND_BASE_URL}/v1/adventures/$adventureId/messages"
        ) {
            header("X-Api-Key", BuildConfig.BACKEND_API_KEY)
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(
                PostTextAdventureMessageV1Request(
                    type = MESSAGE_TYPE_AI,
                    parentId = parentId,
                )
            )
        }.body()
    }

    private fun requireToken(): String =
        sessionProvider.token ?: error("User not authenticated")

    private companion object {
        const val MESSAGE_TYPE_USER = "user"
        const val MESSAGE_TYPE_AI = "AI"
    }
}
