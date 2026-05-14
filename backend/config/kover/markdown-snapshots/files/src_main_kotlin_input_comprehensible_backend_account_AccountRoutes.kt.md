# src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 22-26

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:22-26`

```kotlin
🟢   22 |         val request = call.receive<CredentialsRequest>()
🟢   23 |         val result = accountService.createAccount(request.email, request.password)
🟡   24 |         if (result.payload == null) call.respond(result.status) else call.respond(result.status, result.payload)
⚪   25 |     }
🟢   26 |     post("/v1/auth/sessions") {
```

## Lines 31-39

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:31-39`

```kotlin
🟢   31 |     authenticate("account-bearer") {
🟢   32 |         get("/v1/me") {
🟡   33 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟢   34 |             call.respond(HttpStatusCode.OK, principal.account)
⚪   35 |         }
🟢   36 |         patch("/v1/me") {
🟡   37 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@patch call.respond(HttpStatusCode.Unauthorized)
🟢   38 |             val request = call.receive<UpdateMeRequest>()
🟢   39 |             val result = accountService.updateMe(principal.token, request.email)
```

## Lines 41-56

Location: `src/main/kotlin/input/comprehensible/backend/account/AccountRoutes.kt:41-56`

```kotlin
⚪   41 |         }
🟢   42 |         delete("/v1/me") {
🟡   43 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   44 |             call.respond(accountService.deleteMe(principal.token))
⚪   45 |         }
🟢   46 |         delete("/v1/auth/sessions/current") {
🟡   47 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   48 |             call.respond(accountService.signOutCurrent(principal.token))
⚪   49 |         }
⚪   50 |     }
⚪   51 | }
🟡   52 | @Serializable data class CredentialsRequest(val email: String, val password: String)
🟡   53 | @Serializable data class UpdateMeRequest(val email: String? = null)
🔴   54 | @Serializable
⚪   55 | data class SignInRemoteResponse(
🟢   56 |     @SerialName("access_token") val accessToken: String,
```
