# src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 37-46

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:37-46`

```kotlin
🟢   37 |     authenticate("account-bearer") {
🟢   38 |         get("/v1/me") {
🟡   39 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   40 |             val me = accountService.getMe(principal.accountId) ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟢   41 |             call.respond(HttpStatusCode.OK, me)
⚪   42 |         }
🟢   43 |         patch("/v1/me") {
🟡   44 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@patch call.respond(HttpStatusCode.Unauthorized)
🟢   45 |             val request = call.receive<UpdateMeRequest>()
🟢   46 |             val result = accountService.updateMe(principal.accountId, principal.account.email, request.email, request.password)
```

## Lines 48-57

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:48-57`

```kotlin
⚪   48 |         }
🟢   49 |         delete("/v1/me") {
🟡   50 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   51 |             val request = call.receive<DeleteMeRequest>()
🟢   52 |             call.respond(accountService.deleteMe(principal.accountId, request.password))
⚪   53 |         }
🟢   54 |         delete("/v1/auth/sessions/current") {
🟡   55 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   56 |             call.respond(accountService.signOutCurrent(principal.token))
⚪   57 |         }
```

## Lines 59-68

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:59-68`

```kotlin
⚪   59 | }
⚪   60 | 
🟡   61 | @Serializable data class CredentialsRequest(val email: String, val password: String)
🟡   62 | @Serializable data class UpdateMeRequest(val email: String? = null, val password: String? = null)
🟡   63 | @Serializable data class DeleteMeRequest(val password: String? = null)
🟡   64 | @Serializable data class EmailVerificationRequest(val email: String, val code: String)
⚪   65 | 
🔴   66 | @Serializable
⚪   67 | data class SignInRemoteResponse(
🟢   68 |     @SerialName("access_token") val accessToken: String,
```
