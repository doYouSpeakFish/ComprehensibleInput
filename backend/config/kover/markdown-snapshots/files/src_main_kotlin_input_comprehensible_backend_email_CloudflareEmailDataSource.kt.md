# src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 17-82

Location: `src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt:17-82`

```kotlin
⚪   17 | 
⚪   18 | class CloudflareEmailDataSource(
🔴   19 |     private val from: String,
🔴   20 |     private val accountId: String,
🔴   21 |     private val apiToken: String,
🔴   22 |     private val httpClient: HttpClient = HttpClient(CIO) {
🔴   23 |         install(ContentNegotiation) {
🔴   24 |             json(Json { ignoreUnknownKeys = true })
⚪   25 |         }
⚪   26 |     },
⚪   27 | ) : EmailDataSource {
⚪   28 |     override suspend fun sendEmail(to: String, subject: String, textBody: String) {
🔴   29 |         val response = httpClient.post("https://api.cloudflare.com/client/v4/accounts/$accountId/email/sending/send") {
🔴   30 |             contentType(ContentType.Application.Json)
🔴   31 |             header(HttpHeaders.Authorization, "Bearer $apiToken")
🔴   32 |             setBody(CloudflareSendEmailRequest(to = to, from = from, subject = subject, text = textBody))
🔴   33 |         }
🔴   34 |         val body: CloudflareApiResponse = response.body()
🔴   35 |         require(body.success) { "Cloudflare email send failed" }
⚪   36 |     }
⚪   37 | 
⚪   38 |     companion object {
🔴   39 |         fun fromEnvironment(): CloudflareEmailDataSource = CloudflareEmailDataSource(
🔴   40 |             from = requireEnv("CLOUDFLARE_EMAIL_SENDING_FROM"),
🔴   41 |             accountId = requireEnv("CLOUDFLARE_EMAIL_SENDING_ACCOUNT_ID"),
🔴   42 |             apiToken = requireEnv("CLOUDFLARE_EMAIL_SENDING_TOKEN"),
⚪   43 |         )
⚪   44 | 
⚪   45 |         private fun requireEnv(name: String): String =
🔴   46 |             System.getenv(name)?.takeIf { it.isNotBlank() } ?: error("Missing required environment variable $name")
⚪   47 |     }
⚪   48 | }
⚪   49 | 
🔴   50 | @Serializable
⚪   51 | private data class CloudflareSendEmailRequest(
🔴   52 |     val to: String,
🔴   53 |     val from: String,
🔴   54 |     val subject: String,
🔴   55 |     val text: String,
⚪   56 | )
⚪   57 | 
🔴   58 | @Serializable
⚪   59 | private data class CloudflareApiResponse(
🔴   60 |     val success: Boolean,
🔴   61 |     val result: CloudflareSendEmailResult? = null,
🔴   62 |     val errors: List<CloudflareError> = emptyList(),
🔴   63 |     val messages: List<CloudflareMessage> = emptyList(),
⚪   64 | )
⚪   65 | 
🔴   66 | @Serializable
⚪   67 | private data class CloudflareError(
🔴   68 |     val code: Int,
🔴   69 |     @SerialName("message")
🔴   70 |     val message: String,
⚪   71 | )
⚪   72 | 
🔴   73 | @Serializable
⚪   74 | private data class CloudflareMessage(
🔴   75 |     val code: Int,
🔴   76 |     val message: String,
⚪   77 | )
⚪   78 | 
🔴   79 | @Serializable
⚪   80 | private data class CloudflareSendEmailResult(
🔴   81 |     val id: String? = null,
⚪   82 | )
```
