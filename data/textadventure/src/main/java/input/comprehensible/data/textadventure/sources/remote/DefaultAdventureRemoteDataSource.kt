package input.comprehensible.data.textadventure.sources.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val TIMEOUT_MILLIS = 30_000L

class DefaultAdventureRemoteDataSource(
    private val baseUrl: String,
    private val apiKey: String,
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MILLIS
        }
        install(Logging)
    },
) : AdventureRemoteDataSource {
    override suspend fun getAdventures(token: String): List<RemoteAdventure> {
        val response = httpClient.get("$baseUrl/v1/adventures") {
            header("X-Api-Key", apiKey)
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            error("List adventures failed: ${response.status}")
        }
        return response.body<AdventureListResponse>().items.map { it.toRemoteAdventure() }
    }

    override suspend fun deleteAdventure(token: String, adventureId: String) {
        val response = httpClient.delete("$baseUrl/v1/adventures/$adventureId") {
            header("X-Api-Key", apiKey)
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            error("Delete adventure failed: ${response.status}")
        }
    }
}

@Serializable
private data class AdventureListResponse(
    @SerialName("items") val items: List<AdventureItemResponse>,
)

@Serializable
private data class AdventureItemResponse(
    val id: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
)

private fun AdventureItemResponse.toRemoteAdventure() = RemoteAdventure(
    id = id,
    title = title,
    learningLanguage = learningLanguage,
    translationLanguage = translationLanguage,
    updatedAt = updatedAt,
)
