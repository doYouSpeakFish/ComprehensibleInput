# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 45-93

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:45-93`

```kotlin
⚪   45 | 
⚪   46 | fun main() {
🔴   47 |     val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
🔴   48 |     val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
🔴   49 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   50 |         ?.takeIf { it.isNotBlank() }
🔴   51 |         ?: DEFAULT_DATABASE_URL
⚪   52 | 
🔴   53 |     val databaseUser = System.getenv(BACKEND_DATABASE_USER_ENV_VAR)
🔴   54 |         ?.takeIf { it.isNotBlank() }
🔴   55 |         ?: DEFAULT_APP_DATABASE_USER
🔴   56 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
🔴   57 |     val databaseAdminUser = System.getenv(BACKEND_DATABASE_ADMIN_USER_ENV_VAR)
🔴   58 |         ?.takeIf { it.isNotBlank() }
🔴   59 |         ?: DEFAULT_ADMIN_DATABASE_USER
🔴   60 |     val databaseAdminPassword = requireSecretValue(BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR)
⚪   61 | 
🔴   62 |     val database = connectDatabase(
🔴   63 |         DatabaseConnectionConfig(
🔴   64 |             databaseUrl = databaseUrl,
🔴   65 |             databaseUser = databaseUser,
🔴   66 |             databasePassword = databasePassword,
🔴   67 |             migrationDatabaseUser = databaseAdminUser,
🔴   68 |             migrationDatabasePassword = databaseAdminPassword,
🔴   69 |             appRole = databaseUser,
🔴   70 |             appRolePassword = databasePassword,
⚪   71 |         )
⚪   72 |     )
⚪   73 | 
🔴   74 |     embeddedServer(
🔴   75 |         factory = Netty,
🔴   76 |         port = 8080,
🔴   77 |         host = "0.0.0.0",
⚪   78 |         module = {
🔴   79 |             configureRouting(
🔴   80 |                 textAdventureService = TextAdventureGenerationService(
🔴   81 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   82 |                         apiKey = aiApiKey,
⚪   83 |                     ),
🔴   84 |                     adventureRepository = DatabaseAdventureRepository(
🔴   85 |                         database = database,
⚪   86 |                     ),
⚪   87 |                 ),
🔴   88 |                 appApiKey = apiKey,
⚪   89 |             )
⚪   90 |         },
🔴   91 |     ).start(wait = true)
⚪   92 | }
⚪   93 | 
```

## Lines 120-141

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:120-141`

```kotlin
⚪  120 | 
⚪  121 | private fun requireSecretValue(envVarName: String): String {
🔴  122 |     val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
🔴  123 |     if (directValue != null) {
🔴  124 |         return directValue
⚪  125 |     }
⚪  126 | 
🔴  127 |     val fileEnvVarName = "${envVarName}_FILE"
🔴  128 |     val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
🔴  129 |     if (secretFilePath != null) {
🔴  130 |         val secretValue = File(secretFilePath).readText().trim()
🔴  131 |         require(secretValue.isNotEmpty()) {
🔴  132 |             "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
⚪  133 |         }
🔴  134 |         return secretValue
⚪  135 |     }
⚪  136 | 
🔴  137 |     error(
🔴  138 |         "Missing required environment variable $envVarName. " +
🔴  139 |             "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
⚪  140 |     )
⚪  141 | }
```

## Lines 179-183

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:179-183`

```kotlin
🟢  179 |         authenticate {
🟢  180 |             post("/text-adventures/start") {
🟡  181 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  182 |                 val request = call.receive<StartTextAdventureRequest>()
🟢  183 |                 call.respond(
```

## Lines 190-194

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:190-194`

```kotlin
⚪  190 | 
🟢  191 |             post("/text-adventures/respond") {
🟡  192 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  193 |                 val request = call.receive<ContinueTextAdventureRequest>()
🟢  194 |                 call.respond(
```

## Lines 204-210

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:204-210`

```kotlin
⚪  204 | 
🟢  205 |             get("/text-adventures/{adventureId}/messages") {
🟡  206 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟡  207 |                 val adventureId = requireNotNull(call.parameters["adventureId"]) {
🔴  208 |                     "Missing adventureId path parameter"
⚪  209 |                 }
🟢  210 |                 val response = textAdventureService.getAdventureMessages(adventureId)
```
