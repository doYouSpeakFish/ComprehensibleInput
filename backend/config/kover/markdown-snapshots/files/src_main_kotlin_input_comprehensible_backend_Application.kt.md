# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 33-82

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:33-82`

```kotlin
⚪   33 | 
⚪   34 | fun main() {
🔴   35 |     val aiApiKey = requireNotNull(System.getenv(AI_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   36 |         "Missing required environment variable $AI_API_KEY_ENV_VAR. " +
🔴   37 |             "Set it before starting the backend."
⚪   38 |     }
🔴   39 |     val apiKey = requireNotNull(System.getenv(APP_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   40 |         "Missing required environment variable $APP_API_KEY_ENV_VAR. " +
🔴   41 |             "Set it before starting the backend."
⚪   42 |     }
🔴   43 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   44 |         ?.takeIf { it.isNotBlank() }
🔴   45 |         ?: DEFAULT_DATABASE_URL
⚪   46 | 
🔴   47 |     val databaseUser = requireSecretValue(BACKEND_DATABASE_USER_ENV_VAR)
🔴   48 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
⚪   49 | 
🔴   50 |     migrateDatabase(
🔴   51 |         databaseUrl = databaseUrl,
🔴   52 |         databaseUser = databaseUser,
🔴   53 |         databasePassword = databasePassword,
⚪   54 |     )
⚪   55 | 
🔴   56 |     val database = Database.connect(
🔴   57 |         url = databaseUrl,
🔴   58 |         driver = POSTGRESQL_JDBC_DRIVER,
🔴   59 |         user = databaseUser,
🔴   60 |         password = databasePassword,
⚪   61 |     )
⚪   62 | 
🔴   63 |     embeddedServer(
🔴   64 |         factory = Netty,
🔴   65 |         port = 8080,
🔴   66 |         host = "0.0.0.0",
⚪   67 |         module = {
🔴   68 |             configureRouting(
🔴   69 |                 textAdventureService = TextAdventureGenerationService(
🔴   70 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   71 |                         apiKey = aiApiKey,
⚪   72 |                     ),
🔴   73 |                     adventureRepository = DatabaseAdventureRepository(
🔴   74 |                         database = database,
⚪   75 |                     ),
⚪   76 |                 ),
🔴   77 |                 appApiKey = apiKey,
⚪   78 |             )
⚪   79 |         },
🔴   80 |     ).start(wait = true)
⚪   81 | }
⚪   82 | 
```

## Lines 86-115

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:86-115`

```kotlin
⚪   86 |     databasePassword: String,
⚪   87 | ) {
🔴   88 |     Flyway.configure()
🔴   89 |         .dataSource(databaseUrl, databaseUser, databasePassword)
🔴   90 |         .load()
🔴   91 |         .migrate()
⚪   92 | }
⚪   93 | 
⚪   94 | 
⚪   95 | private fun requireSecretValue(envVarName: String): String {
🔴   96 |     val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
🔴   97 |     if (directValue != null) {
🔴   98 |         return directValue
⚪   99 |     }
⚪  100 | 
🔴  101 |     val fileEnvVarName = "${envVarName}_FILE"
🔴  102 |     val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
🔴  103 |     if (secretFilePath != null) {
🔴  104 |         val secretValue = File(secretFilePath).readText().trim()
🔴  105 |         require(secretValue.isNotEmpty()) {
🔴  106 |             "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
⚪  107 |         }
🔴  108 |         return secretValue
⚪  109 |     }
⚪  110 | 
🔴  111 |     error(
🔴  112 |         "Missing required environment variable $envVarName. " +
🔴  113 |             "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
⚪  114 |     )
⚪  115 | }
```

## Lines 153-157

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:153-157`

```kotlin
🟢  153 |         authenticate {
🟢  154 |             post("/text-adventures/start") {
🟡  155 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  156 |                 val request = call.receive<StartTextAdventureRequest>()
🟢  157 |                 call.respond(
```

## Lines 164-168

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:164-168`

```kotlin
⚪  164 | 
🟢  165 |             post("/text-adventures/respond") {
🟡  166 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  167 |                 val request = call.receive<ContinueTextAdventureRequest>()
🟢  168 |                 call.respond(
```

## Lines 178-184

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:178-184`

```kotlin
⚪  178 | 
🟢  179 |             get("/text-adventures/{adventureId}/messages") {
🟡  180 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟡  181 |                 val adventureId = requireNotNull(call.parameters["adventureId"]) {
🔴  182 |                     "Missing adventureId path parameter"
⚪  183 |                 }
🟢  184 |                 val response = textAdventureService.getAdventureMessages(adventureId)
```
