# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 22-44

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:22-44`

```kotlin
⚪   22 | 
⚪   23 | fun main() {
🔴   24 |     val apiKey = requireNotNull(System.getenv(AI_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   25 |         "Missing required environment variable $AI_API_KEY_ENV_VAR. " +
🔴   26 |                 "Set it before starting the backend."
⚪   27 |     }
⚪   28 | 
🔴   29 |     embeddedServer(
🔴   30 |         factory = Netty,
🔴   31 |         port = 8080,
🔴   32 |         host = "0.0.0.0",
⚪   33 |         module = {
🔴   34 |             configureRouting(
🔴   35 |                 textAdventureService = TextAdventureGenerationService(
🔴   36 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   37 |                         apiKey = apiKey,
⚪   38 |                     )
⚪   39 |                 )
⚪   40 |             )
⚪   41 |         },
🔴   42 |     ).start(wait = true)
⚪   43 | }
⚪   44 | 
```

## Lines 70-81

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:70-81`

```kotlin
⚪   70 | 
🟢   71 |         post("/text-adventures/respond") {
🔴   72 |             val request = call.receive<ContinueTextAdventureRequest>()
🔴   73 |             call.respond(
🔴   74 |                 textAdventureService.respondToUser(
🔴   75 |                     adventureId = request.adventureId,
🔴   76 |                     learningLanguage = request.learningLanguage,
🔴   77 |                     translationsLanguage = request.translationsLanguage,
🔴   78 |                     userMessage = request.userMessage,
🔴   79 |                     history = request.history,
⚪   80 |                 )
⚪   81 |             )
```
