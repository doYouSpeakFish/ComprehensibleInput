# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 17-23

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:17-23`

```kotlin
🟢   17 |     authenticate("account-bearer") {
🟢   18 |         post("/v1/adventures") {
🟡   19 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟢   20 |             val request = call.receive<StartTextAdventureV1Request>()
🟡   21 |             if (request.learningLanguage.isBlank() || request.translationLanguage.isBlank()) {
🟢   22 |                 return@post call.respond(HttpStatusCode.BadRequest)
⚪   23 |             }
```

## Lines 31-35

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:31-35`

```kotlin
⚪   31 | 
🟢   32 |         get("/v1/adventures") {
🟡   33 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟢   34 |             call.respond(textAdventureService.listAdventuresForAccount(principal.accountId))
⚪   35 |         }
```

## Lines 38-43

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:38-43`

```kotlin
⚪   38 | 
🟢   39 |         get("/v1/adventures/{adventureId}") {
🟡   40 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   41 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   42 |             val response = textAdventureService.getAdventureSummaryForAccount(
🟢   43 |                 accountId = principal.accountId,
```

## Lines 48-53

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:48-53`

```kotlin
⚪   48 | 
🟢   49 |         get("/v1/adventures/{adventureId}/messages") {
🟡   50 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   51 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   52 |             val response = textAdventureService.getAdventureMessagesForAccount(
🟢   53 |                 accountId = principal.accountId,
```

## Lines 58-63

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:58-63`

```kotlin
⚪   58 | 
🟢   59 |         post("/v1/adventures/{adventureId}/messages") {
🟡   60 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟡   61 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   62 |             val request = call.receive<RespondTextAdventureV1Request>()
🟢   63 |             if (request.playerText.isBlank()) {
```

## Lines 77-82

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:77-82`

```kotlin
⚪   77 | 
🟢   78 |         delete("/v1/adventures/{adventureId}") {
🟡   79 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟡   80 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   81 |             val deleted = textAdventureService.deleteAdventureForAccount(
🟢   82 |                 accountId = principal.accountId,
```

## Lines 91-95

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:91-95`

```kotlin
⚪   91 | 
🟢   92 |         delete("/v1/adventures") {
🟡   93 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   94 |             textAdventureService.deleteAllAdventuresForAccount(principal.accountId)
🟢   95 |             call.respond(HttpStatusCode.NoContent)
```

## Lines 98-102

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:98-102`

```kotlin
⚪   98 | }
⚪   99 | 
🟡  100 | @Serializable
⚪  101 | private data class StartTextAdventureV1Request(
🟢  102 |     val learningLanguage: String,
```

## Lines 104-108

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:104-108`

```kotlin
⚪  104 | )
⚪  105 | 
🟡  106 | @Serializable
⚪  107 | private data class RespondTextAdventureV1Request(
🟢  108 |     val playerText: String,
```
