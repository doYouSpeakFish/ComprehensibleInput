# src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureRoutes.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 16-25

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureRoutes.kt:16-25`

```kotlin
🟢   16 |     authenticate {
🟢   17 |         post("/text-adventures/start") {
🟡   18 |             requireNotNull(call.principal<AppPrincipal>())
🟢   19 |             val request = call.receive<StartTextAdventureRequest>()
🟢   20 |             call.respond(textAdventureService.startAdventure(request.learningLanguage, request.translationsLanguage))
⚪   21 |         }
🟢   22 |         post("/text-adventures/respond") {
🟡   23 |             requireNotNull(call.principal<AppPrincipal>())
🟢   24 |             val request = call.receive<ContinueTextAdventureRequest>()
🟢   25 |             call.respond(
```

## Lines 34-39

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/TextAdventureRoutes.kt:34-39`

```kotlin
⚪   34 |         }
🟢   35 |         get("/text-adventures/{adventureId}/messages") {
🟡   36 |             requireNotNull(call.principal<AppPrincipal>())
🟡   37 |             val adventureId = requireNotNull(call.parameters["adventureId"])
🟢   38 |             val response = textAdventureService.getAdventureMessages(adventureId)
🟢   39 |             if (response == null) call.respond(HttpStatusCode.NotFound) else call.respond(response)
```
