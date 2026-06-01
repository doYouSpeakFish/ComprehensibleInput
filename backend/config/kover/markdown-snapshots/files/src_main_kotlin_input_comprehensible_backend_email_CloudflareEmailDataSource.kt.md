# src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 18-80

Location: `src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt:18-80`

```kotlin
⚪   18 | 
⚪   19 | class CloudflareEmailDataSource(
🔴   20 |     private val from: String,
🔴   21 |     private val accountId: String,
🔴   22 |     private val apiToken: String,
🔴   23 |     private val httpClient: HttpClient = HttpClient(CIO) {
🔴   24 |         install(ContentNegotiation) {
🔴   25 |             json(Json { ignoreUnknownKeys = true })
⚪   26 |         }
⚪   27 |     },
⚪   28 | ) : EmailDataSource {
⚪   29 |     override suspend fun sendEmail(to: String, subject: String, textBody: String) {
🔴   30 |         val response = httpClient.post("https://api.cloudflare.com/client/v4/accounts/$accountId/email/sending/send") {
🔴   31 |             contentType(ContentType.Application.Json)
🔴   32 |             header(HttpHeaders.Authorization, "Bearer $apiToken")
🔴   33 |             setBody(CloudflareSendEmailRequest(to = to, from = from, subject = subject, text = textBody))
🔴   34 |         }
🔴   35 |         val body: CloudflareApiResponse = response.body()
🔴   36 |         require(body.success) { "Cloudflare email send failed" }
⚪   37 |     }
⚪   38 | 
⚪   39 |     companion object {
🔴   40 |         fun fromEnvironment(): CloudflareEmailDataSource = CloudflareEmailDataSource(
🔴   41 |             from = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_FROM"),
🔴   42 |             accountId = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_ACCOUNT_ID"),
🔴   43 |             apiToken = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_TOKEN"),
⚪   44 |         )
⚪   45 |     }
⚪   46 | }
⚪   47 | 
🔴   48 | @Serializable
⚪   49 | private data class CloudflareSendEmailRequest(
🔴   50 |     val to: String,
🔴   51 |     val from: String,
🔴   52 |     val subject: String,
🔴   53 |     val text: String,
⚪   54 | )
⚪   55 | 
🔴   56 | @Serializable
⚪   57 | private data class CloudflareApiResponse(
🔴   58 |     val success: Boolean,
🔴   59 |     val result: CloudflareSendEmailResult? = null,
🔴   60 |     val errors: List<CloudflareError> = emptyList(),
🔴   61 |     val messages: List<CloudflareMessage> = emptyList(),
⚪   62 | )
⚪   63 | 
🔴   64 | @Serializable
⚪   65 | private data class CloudflareError(
🔴   66 |     val code: Int,
🔴   67 |     @SerialName("message")
🔴   68 |     val message: String,
⚪   69 | )
⚪   70 | 
🔴   71 | @Serializable
⚪   72 | private data class CloudflareMessage(
🔴   73 |     val code: Int,
🔴   74 |     val message: String,
⚪   75 | )
⚪   76 | 
🔴   77 | @Serializable
⚪   78 | private data class CloudflareSendEmailResult(
🔴   79 |     val id: String? = null,
⚪   80 | )
```
