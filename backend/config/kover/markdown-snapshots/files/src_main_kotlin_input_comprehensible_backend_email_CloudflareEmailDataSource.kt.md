# src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 18-83

Location: `src/main/kotlin/input/comprehensible/backend/email/CloudflareEmailDataSource.kt:18-83`

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
🔴   36 |         require(body.success) {
🔴   37 |             val errorDetails = body.errors.joinToString("; ") { "[${it.code}] ${it.message}" }
🔴   38 |             "Cloudflare email send failed: $errorDetails"
⚪   39 |         }
⚪   40 |     }
⚪   41 | 
⚪   42 |     companion object {
🔴   43 |         fun fromEnvironment(): CloudflareEmailDataSource = CloudflareEmailDataSource(
🔴   44 |             from = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_FROM"),
🔴   45 |             accountId = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_ACCOUNT_ID"),
🔴   46 |             apiToken = requireSecretValue("CLOUDFLARE_EMAIL_SENDING_TOKEN"),
⚪   47 |         )
⚪   48 |     }
⚪   49 | }
⚪   50 | 
🔴   51 | @Serializable
⚪   52 | private data class CloudflareSendEmailRequest(
🔴   53 |     val to: String,
🔴   54 |     val from: String,
🔴   55 |     val subject: String,
🔴   56 |     val text: String,
⚪   57 | )
⚪   58 | 
🔴   59 | @Serializable
⚪   60 | private data class CloudflareApiResponse(
🔴   61 |     val success: Boolean,
🔴   62 |     val result: CloudflareSendEmailResult? = null,
🔴   63 |     val errors: List<CloudflareError> = emptyList(),
🔴   64 |     val messages: List<CloudflareMessage> = emptyList(),
⚪   65 | )
⚪   66 | 
🔴   67 | @Serializable
⚪   68 | private data class CloudflareError(
🔴   69 |     val code: Int,
🔴   70 |     @SerialName("message")
🔴   71 |     val message: String,
⚪   72 | )
⚪   73 | 
🔴   74 | @Serializable
⚪   75 | private data class CloudflareMessage(
🔴   76 |     val code: Int,
🔴   77 |     val message: String,
⚪   78 | )
⚪   79 | 
🔴   80 | @Serializable
⚪   81 | private data class CloudflareSendEmailResult(
🔴   82 |     val id: String? = null,
⚪   83 | )
```
