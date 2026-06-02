# src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 55-70

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:55-70`

```kotlin
🟢   55 |     authenticate("account-bearer") {
🟢   56 |         get("/v1/me") {
🟡   57 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   58 |             val me = accountService.getMe(principal.accountId) ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟢   59 |             call.respond(HttpStatusCode.OK, me)
⚪   60 |         }
🟢   61 |         patch("/v1/me") {
🟡   62 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@patch call.respond(HttpStatusCode.Unauthorized)
🟢   63 |             val request = call.receive<UpdateMeRequest>()
🟢   64 |             call.respond(accountService.updateMe(accountId = principal.accountId, newEmail = request.email, password = request.password))
⚪   65 |         }
🟢   66 |         post("/v1/email-change-current-verifications") {
🟡   67 |             val principal = call.principal<AccountSessionPrincipal>()
🔴   68 |                 ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   69 |             val request = call.receive<EmailChangeCurrentVerificationRequest>()
🟢   70 |             call.respond(accountService.verifyCurrentEmailChange(accountId = principal.accountId, code = request.code))
```

## Lines 72-83

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:72-83`

```kotlin
🟢   72 |         rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("email-change-current-verification-code")) {
🟢   73 |             post("/v1/email-change-current-verification-codes") {
🟡   74 |                 val principal = call.principal<AccountSessionPrincipal>()
🔴   75 |                     ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   76 |                 call.respond(accountService.requestNewEmailChangeCurrentCode(principal.accountId))
⚪   77 |             }
⚪   78 |         }
🟢   79 |         post("/v1/email-change-verifications") {
🟡   80 |             val principal = call.principal<AccountSessionPrincipal>()
🔴   81 |                 ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   82 |             val request = call.receive<EmailVerificationRequest>()
🟢   83 |             call.respond(accountService.verifyPendingEmailChange(principal.accountId, request.email, request.code))
```

## Lines 85-100

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:85-100`

```kotlin
🟢   85 |         rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("email-change-new-verification-code")) {
🟢   86 |             post("/v1/email-change-new-verification-codes") {
🟡   87 |                 val principal = call.principal<AccountSessionPrincipal>()
🔴   88 |                     ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   89 |                 call.respond(accountService.requestNewEmailChangeNewEmailCode(principal.accountId))
⚪   90 |             }
⚪   91 |         }
🟢   92 |         delete("/v1/me") {
🟡   93 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   94 |             val request = call.receive<DeleteMeRequest>()
🟢   95 |             call.respond(accountService.deleteMe(principal.accountId, request.password))
⚪   96 |         }
🟢   97 |         delete("/v1/auth/sessions/current") {
🟡   98 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   99 |             call.respond(accountService.signOutCurrent(principal.token))
⚪  100 |         }
```

## Lines 102-115

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:102-115`

```kotlin
⚪  102 | }
⚪  103 | 
🟡  104 | @Serializable data class EmailVerificationCodeRequest(val email: String)
🟡  105 | @Serializable data class CredentialsRequest(val email: String, val password: String)
🟡  106 | @Serializable data class UpdateMeRequest(val email: String? = null, val password: String? = null)
🟡  107 | @Serializable data class DeleteMeRequest(val password: String? = null)
🟡  108 | @Serializable data class EmailVerificationRequest(val email: String, val code: String)
🟡  109 | @Serializable data class EmailChangeCurrentVerificationRequest(val code: String)
🟡  110 | @Serializable data class PasswordResetCodeRequest(val email: String)
🟡  111 | @Serializable data class PasswordResetRequest(val email: String, val password: String, val code: String)
⚪  112 | 
🔴  113 | @Serializable
⚪  114 | data class SignInRemoteResponse(
🟢  115 |     @SerialName("access_token") val accessToken: String,
```
