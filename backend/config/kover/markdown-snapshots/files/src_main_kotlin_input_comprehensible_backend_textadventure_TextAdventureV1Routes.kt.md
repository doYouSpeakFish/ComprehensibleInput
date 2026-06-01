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

## Lines 31-41

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:31-41`

```kotlin
⚪   31 | 
🟢   32 |         get("/v1/adventures") {
🟡   33 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟢   34 |             call.respond(textAdventureService.listAdventuresForAccount(principal.accountId))
⚪   35 |         }
⚪   36 | 
🟢   37 |         get("/v1/adventures/{adventureId}") {
🟡   38 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   39 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   40 |             val response = textAdventureService.getAdventureSummaryForAccount(
🟢   41 |                 accountId = principal.accountId,
```

## Lines 46-51

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:46-51`

```kotlin
⚪   46 | 
🟢   47 |         get("/v1/adventures/{adventureId}/messages") {
🟡   48 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
🟡   49 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   50 |             val response = textAdventureService.getAdventureMessagesForAccount(
🟢   51 |                 accountId = principal.accountId,
```

## Lines 56-64

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:56-64`

```kotlin
⚪   56 | 
🟢   57 |         post("/v1/adventures/{adventureId}/messages") {
🟡   58 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
🟡   59 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   60 |             val request = call.receive<PostTextAdventureMessageV1Request>()
🟡   61 |             if (request.parentId.isBlank()) {
🔴   62 |                 return@post call.respond(HttpStatusCode.BadRequest)
⚪   63 |             }
🟢   64 |             when (request.type) {
```

## Lines 80-85

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:80-85`

```kotlin
⚪   80 | 
🟢   81 |         delete("/v1/adventures/{adventureId}") {
🟡   82 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟡   83 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   84 |             val deleted = textAdventureService.deleteAdventureForAccount(
🟢   85 |                 accountId = principal.accountId,
```

## Lines 94-98

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:94-98`

```kotlin
⚪   94 | 
🟢   95 |         delete("/v1/adventures") {
🟡   96 |             val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
🟢   97 |             textAdventureService.deleteAllAdventuresForAccount(principal.accountId)
🟢   98 |             call.respond(HttpStatusCode.NoContent)
```

## Lines 101-105

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:101-105`

```kotlin
⚪  101 | }
⚪  102 | 
🟡  103 | @Serializable
⚪  104 | private data class StartTextAdventureV1Request(
🟢  105 |     val learningLanguage: String,
```

## Lines 107-111

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:107-111`

```kotlin
⚪  107 | )
⚪  108 | 
🟡  109 | @Serializable
⚪  110 | private data class PostTextAdventureMessageV1Request(
🟢  111 |     val type: String,
```

## Lines 121-125

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:121-125`

```kotlin
⚪  121 | ) {
🟢  122 |     val text = request.text
🟡  123 |     if (text.isNullOrBlank()) {
🟢  124 |         return respond(HttpStatusCode.BadRequest)
⚪  125 |     }
```

## Lines 147-151

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureV1Routes.kt:147-151`

```kotlin
🟢  147 |         parentMessageId = request.parentId,
⚪  148 |     )
🟡  149 |     if (response == null) respond(HttpStatusCode.NotFound) else respond(HttpStatusCode.Created, response)
⚪  150 | }
⚪  151 | 
```
