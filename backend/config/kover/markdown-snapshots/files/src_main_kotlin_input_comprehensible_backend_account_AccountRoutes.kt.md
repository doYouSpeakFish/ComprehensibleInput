# src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 49-80

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:49-80`

```kotlin
🟢   49 |     authenticate("account-bearer") {
🟢   50 |         get("/v1/me") {
🟡   51 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   52 |             val me = accountService.getMe(principal.accountId) ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟢   53 |             call.respond(HttpStatusCode.OK, me)
⚪   54 |         }
🟢   55 |         patch("/v1/me") {
🟡   56 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@patch call.respond(HttpStatusCode.Unauthorized)
🟢   57 |             val request = call.receive<UpdateMeRequest>()
🟢   58 |             call.respond(accountService.updateMe(accountId = principal.accountId, newEmail = request.email, password = request.password))
⚪   59 |         }
🟢   60 |         post("/v1/email-change-current-verifications") {
🟡   61 |             val principal = call.principal<AccountSessionPrincipal>()
🔴   62 |                 ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   63 |             val request = call.receive<EmailChangeCurrentVerificationRequest>()
🟢   64 |             call.respond(accountService.verifyCurrentEmailChange(accountId = principal.accountId, code = request.code))
⚪   65 |         }
🟢   66 |         post("/v1/email-change-verifications") {
🟡   67 |             val principal = call.principal<AccountSessionPrincipal>()
🔴   68 |                 ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   69 |             val request = call.receive<EmailVerificationRequest>()
🟢   70 |             call.respond(accountService.verifyPendingEmailChange(principal.accountId, request.email, request.code))
⚪   71 |         }
🟢   72 |         delete("/v1/me") {
🟡   73 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   74 |             val request = call.receive<DeleteMeRequest>()
🟢   75 |             call.respond(accountService.deleteMe(principal.accountId, request.password))
⚪   76 |         }
🟢   77 |         delete("/v1/auth/sessions/current") {
🟡   78 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   79 |             call.respond(accountService.signOutCurrent(principal.token))
⚪   80 |         }
```

## Lines 82-94

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:82-94`

```kotlin
⚪   82 | }
⚪   83 | 
🟡   84 | @Serializable data class CredentialsRequest(val email: String, val password: String)
🟡   85 | @Serializable data class UpdateMeRequest(val email: String? = null, val password: String? = null)
🟡   86 | @Serializable data class DeleteMeRequest(val password: String? = null)
🟡   87 | @Serializable data class EmailVerificationRequest(val email: String, val code: String)
🟡   88 | @Serializable data class EmailChangeCurrentVerificationRequest(val code: String)
🟡   89 | @Serializable data class PasswordResetCodeRequest(val email: String)
🟡   90 | @Serializable data class PasswordResetRequest(val email: String, val password: String, val code: String)
⚪   91 | 
🔴   92 | @Serializable
⚪   93 | data class SignInRemoteResponse(
🟢   94 |     @SerialName("access_token") val accessToken: String,
```
