# src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 47-78

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:47-78`

```kotlin
🟢   47 |     authenticate("account-bearer") {
🟢   48 |         get("/v1/me") {
🟡   49 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   50 |             val me = accountService.getMe(principal.accountId) ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟢   51 |             call.respond(HttpStatusCode.OK, me)
⚪   52 |         }
🟢   53 |         patch("/v1/me") {
🟡   54 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@patch call.respond(HttpStatusCode.Unauthorized)
🟢   55 |             val request = call.receive<UpdateMeRequest>()
🟢   56 |             call.respond(accountService.updateMe(accountId = principal.accountId, newEmail = request.email, password = request.password))
⚪   57 |         }
🟢   58 |         post("/v1/email-change-current-verifications") {
🟡   59 |             val principal = call.principal<AccountSessionPrincipal>()
🔴   60 |                 ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   61 |             val request = call.receive<EmailChangeCurrentVerificationRequest>()
🟢   62 |             call.respond(accountService.verifyCurrentEmailChange(accountId = principal.accountId, code = request.code))
⚪   63 |         }
🟢   64 |         post("/v1/email-change-verifications") {
🟡   65 |             val principal = call.principal<AccountSessionPrincipal>()
🔴   66 |                 ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   67 |             val request = call.receive<EmailVerificationRequest>()
🟢   68 |             call.respond(accountService.verifyPendingEmailChange(principal.accountId, request.email, request.code))
⚪   69 |         }
🟢   70 |         delete("/v1/me") {
🟡   71 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   72 |             val request = call.receive<DeleteMeRequest>()
🟢   73 |             call.respond(accountService.deleteMe(principal.accountId, request.password))
⚪   74 |         }
🟢   75 |         delete("/v1/auth/sessions/current") {
🟡   76 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   77 |             call.respond(accountService.signOutCurrent(principal.token))
⚪   78 |         }
```

## Lines 80-92

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:80-92`

```kotlin
⚪   80 | }
⚪   81 | 
🟡   82 | @Serializable data class CredentialsRequest(val email: String, val password: String)
🟡   83 | @Serializable data class UpdateMeRequest(val email: String? = null, val password: String? = null)
🟡   84 | @Serializable data class DeleteMeRequest(val password: String? = null)
🟡   85 | @Serializable data class EmailVerificationRequest(val email: String, val code: String)
🟡   86 | @Serializable data class EmailChangeCurrentVerificationRequest(val code: String)
🟡   87 | @Serializable data class PasswordResetCodeRequest(val email: String)
🟡   88 | @Serializable data class PasswordResetRequest(val email: String, val password: String, val code: String)
⚪   89 | 
🔴   90 | @Serializable
⚪   91 | data class SignInRemoteResponse(
🟢   92 |     @SerialName("access_token") val accessToken: String,
```
