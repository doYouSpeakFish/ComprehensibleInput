package input.comprehensible.backend.email

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import input.comprehensible.backend.common.requireSecretValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class CloudflareEmailDataSource(
    private val from: String,
    private val accountId: String,
    private val apiToken: String,
    private val httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    },
) : EmailDataSource {
    override suspend fun sendEmail(to: String, subject: String, textBody: String) {
        val response = httpClient.post("https://api.cloudflare.com/client/v4/accounts/$accountId/email/sending/send") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $apiToken")
            setBody(CloudflareSendEmailRequest(to = to, from = from, subject = subject, text = textBody))
        }
        val body: CloudflareApiResponse = response.body()
        require(body.success) {
            val errorDetails = body.errors.joinToString("; ") { "[${it.code}] ${it.message}" }
            "Cloudflare email send failed: $errorDetails"
        }
    }

    companion object {
        fun fromEnvironment(): CloudflareEmailDataSource = CloudflareEmailDataSource(
            from = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_FROM"),
            accountId = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_ACCOUNT_ID"),
            apiToken = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_TOKEN"),
        )
    }
}

@Serializable
private data class CloudflareSendEmailRequest(
    val to: String,
    val from: String,
    val subject: String,
    val text: String,
)

@Serializable
private data class CloudflareApiResponse(
    val success: Boolean,
    val result: CloudflareSendEmailResult? = null,
    val errors: List<CloudflareError> = emptyList(),
    val messages: List<CloudflareMessage> = emptyList(),
)

@Serializable
private data class CloudflareError(
    val code: Int,
    @SerialName("message")
    val message: String,
)

@Serializable
private data class CloudflareMessage(
    val code: Int,
    val message: String,
)

@Serializable
private data class CloudflareSendEmailResult(
    val id: String? = null,
)
