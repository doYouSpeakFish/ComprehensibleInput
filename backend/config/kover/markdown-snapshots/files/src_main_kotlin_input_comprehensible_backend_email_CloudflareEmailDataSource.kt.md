# src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 16-66

Location: `src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt:16-66`

```kotlin
⚪   16 | 
⚪   17 | class CloudflareEmailDataSource(
🔴   18 |     private val from: String,
🔴   19 |     private val accountId: String,
🔴   20 |     private val apiToken: String,
🔴   21 |     private val httpClient: HttpClient = HttpClient(CIO) {
🔴   22 |         install(ContentNegotiation) { json() }
⚪   23 |     },
⚪   24 | ) : EmailDataSource {
⚪   25 |     override suspend fun sendEmail(to: String, subject: String, textBody: String) {
🔴   26 |         val response = httpClient.post("https://api.cloudflare.com/client/v4/accounts/$accountId/email/sending/send") {
🔴   27 |             contentType(ContentType.Application.Json)
🔴   28 |             header(HttpHeaders.Authorization, "Bearer $apiToken")
🔴   29 |             setBody(CloudflareSendEmailRequest(to = to, from = from, subject = subject, text = textBody))
🔴   30 |         }
🔴   31 |         val body: CloudflareApiResponse = response.body()
🔴   32 |         require(body.success) { "Cloudflare email send failed: ${body.errors.joinToString { it.message }}" }
⚪   33 |     }
⚪   34 | 
⚪   35 |     companion object {
🔴   36 |         fun fromEnvironment(): CloudflareEmailDataSource = CloudflareEmailDataSource(
🔴   37 |             from = requireEnv("CLOUDFLARE_EMAIL_SENDING_FROM"),
🔴   38 |             accountId = requireEnv("CLOUDFLARE_EMAIL_SENDING_ACCOUNT_ID"),
🔴   39 |             apiToken = requireEnv("CLOUDFLARE_EMAIL_SENDING_TOKEN"),
⚪   40 |         )
⚪   41 | 
⚪   42 |         private fun requireEnv(name: String): String =
🔴   43 |             System.getenv(name)?.takeIf { it.isNotBlank() } ?: error("Missing required environment variable $name")
⚪   44 |     }
⚪   45 | }
⚪   46 | 
🔴   47 | @Serializable
⚪   48 | private data class CloudflareSendEmailRequest(
🔴   49 |     val to: String,
🔴   50 |     val from: String,
🔴   51 |     val subject: String,
🔴   52 |     val text: String,
⚪   53 | )
⚪   54 | 
🔴   55 | @Serializable
⚪   56 | private data class CloudflareApiResponse(
🔴   57 |     val success: Boolean,
🔴   58 |     val errors: List<CloudflareError> = emptyList(),
⚪   59 | )
⚪   60 | 
🔴   61 | @Serializable
⚪   62 | private data class CloudflareError(
🔴   63 |     val code: Int,
🔴   64 |     @SerialName("message")
🔴   65 |     val message: String,
⚪   66 | )
```
