# src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 47-56

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:47-56`

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
🟢   56 |             val result = accountService.updateMe(principal.accountId, principal.account.email, request.email, request.password)
```

## Lines 58-67

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:58-67`

```kotlin
⚪   58 |         }
🟢   59 |         delete("/v1/me") {
🟡   60 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   61 |             val request = call.receive<DeleteMeRequest>()
🟢   62 |             call.respond(accountService.deleteMe(principal.accountId, request.password))
⚪   63 |         }
🟢   64 |         delete("/v1/auth/sessions/current") {
🟡   65 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   66 |             call.respond(accountService.signOutCurrent(principal.token))
⚪   67 |         }
```

## Lines 69-80

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:69-80`

```kotlin
⚪   69 | }
⚪   70 | 
🟡   71 | @Serializable data class CredentialsRequest(val email: String, val password: String)
🟡   72 | @Serializable data class UpdateMeRequest(val email: String? = null, val password: String? = null)
🟡   73 | @Serializable data class DeleteMeRequest(val password: String? = null)
🟡   74 | @Serializable data class EmailVerificationRequest(val email: String, val code: String)
🟡   75 | @Serializable data class PasswordResetCodeRequest(val email: String)
🟡   76 | @Serializable data class PasswordResetRequest(val email: String, val password: String, val code: String)
⚪   77 | 
🔴   78 | @Serializable
⚪   79 | data class SignInRemoteResponse(
🟢   80 |     @SerialName("access_token") val accessToken: String,
```
