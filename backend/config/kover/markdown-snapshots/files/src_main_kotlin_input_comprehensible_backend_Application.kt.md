# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 29-56

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:29-56`

```kotlin
⚪   29 | 
⚪   30 | fun main() {
🔴   31 |     val aiApiKey = requireNotNull(System.getenv(AI_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   32 |         "Missing required environment variable $AI_API_KEY_ENV_VAR. " +
🔴   33 |                 "Set it before starting the backend."
⚪   34 |     }
🔴   35 |     val apiKey = requireNotNull(System.getenv(APP_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   36 |         "Missing required environment variable $APP_API_KEY_ENV_VAR. " +
🔴   37 |                 "Set it before starting the backend."
⚪   38 |     }
⚪   39 | 
🔴   40 |     embeddedServer(
🔴   41 |         factory = Netty,
🔴   42 |         port = 8080,
🔴   43 |         host = "0.0.0.0",
⚪   44 |         module = {
🔴   45 |             configureRouting(
🔴   46 |                 textAdventureService = TextAdventureGenerationService(
🔴   47 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   48 |                         apiKey = aiApiKey,
⚪   49 |                     )
⚪   50 |                 ),
🔴   51 |                 appApiKey = apiKey,
⚪   52 |             )
⚪   53 |         },
🔴   54 |     ).start(wait = true)
⚪   55 | }
⚪   56 | 
```

## Lines 93-97

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:93-97`

```kotlin
🟢   93 |         authenticate {
🟢   94 |             post("/text-adventures/start") {
🟡   95 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢   96 |                 val request = call.receive<StartTextAdventureRequest>()
🟢   97 |                 call.respond(
```

## Lines 104-116

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:104-116`

```kotlin
⚪  104 | 
🟢  105 |             post("/text-adventures/respond") {
🔴  106 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🔴  107 |                 val request = call.receive<ContinueTextAdventureRequest>()
🔴  108 |                 call.respond(
🔴  109 |                     textAdventureService.respondToUser(
🔴  110 |                         adventureId = request.adventureId,
🔴  111 |                         learningLanguage = request.learningLanguage,
🔴  112 |                         translationsLanguage = request.translationsLanguage,
🔴  113 |                         userMessage = request.userMessage,
🔴  114 |                         history = request.history,
⚪  115 |                     )
⚪  116 |                 )
```
