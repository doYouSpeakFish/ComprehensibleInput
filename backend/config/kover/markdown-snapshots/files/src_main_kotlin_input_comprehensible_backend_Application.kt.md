# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 33-129

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:33-129`

```kotlin
⚪   33 | 
⚪   34 | fun main() {
🔴   35 |     val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
🔴   36 |     val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
🔴   37 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   38 |         ?.takeIf { it.isNotBlank() }
🔴   39 |         ?: DEFAULT_DATABASE_URL
⚪   40 | 
🔴   41 |     val databaseUser = System.getenv(BACKEND_DATABASE_USER_ENV_VAR)
🔴   42 |         ?.takeIf { it.isNotBlank() }
🔴   43 |         ?: DEFAULT_APP_DATABASE_USER
🔴   44 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
🔴   45 |     val databaseAdminUser = System.getenv(BACKEND_DATABASE_ADMIN_USER_ENV_VAR)
🔴   46 |         ?.takeIf { it.isNotBlank() }
🔴   47 |         ?: DEFAULT_ADMIN_DATABASE_USER
🔴   48 |     val databaseAdminPassword = requireSecretValue(BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR)
⚪   49 | 
🔴   50 |     migrateDatabase(
🔴   51 |         config = MigrationConfig(
🔴   52 |             databaseUrl = databaseUrl,
🔴   53 |             migrationDatabaseUser = databaseAdminUser,
🔴   54 |             migrationDatabasePassword = databaseAdminPassword,
🔴   55 |             appRole = databaseUser,
🔴   56 |             appRolePassword = databasePassword,
⚪   57 |         )
⚪   58 |     )
⚪   59 | 
🔴   60 |     val database = Database.connect(
🔴   61 |         url = databaseUrl,
🔴   62 |         driver = POSTGRESQL_JDBC_DRIVER,
🔴   63 |         user = databaseUser,
🔴   64 |         password = databasePassword,
⚪   65 |     )
⚪   66 | 
🔴   67 |     embeddedServer(
🔴   68 |         factory = Netty,
🔴   69 |         port = 8080,
🔴   70 |         host = "0.0.0.0",
⚪   71 |         module = {
🔴   72 |             configureRouting(
🔴   73 |                 textAdventureService = TextAdventureGenerationService(
🔴   74 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   75 |                         apiKey = aiApiKey,
⚪   76 |                     ),
🔴   77 |                     adventureRepository = DatabaseAdventureRepository(
🔴   78 |                         database = database,
⚪   79 |                     ),
⚪   80 |                 ),
🔴   81 |                 appApiKey = apiKey,
⚪   82 |             )
⚪   83 |         },
🔴   84 |     ).start(wait = true)
⚪   85 | }
⚪   86 | 
⚪   87 | 
🔴   88 | private data class MigrationConfig(
🔴   89 |     val databaseUrl: String,
🔴   90 |     val migrationDatabaseUser: String,
🔴   91 |     val migrationDatabasePassword: String,
🔴   92 |     val appRole: String,
🔴   93 |     val appRolePassword: String,
⚪   94 | )
⚪   95 | 
⚪   96 | private fun migrateDatabase(config: MigrationConfig) {
🔴   97 |     Flyway.configure()
🔴   98 |         .dataSource(config.databaseUrl, config.migrationDatabaseUser, config.migrationDatabasePassword)
🔴   99 |         .placeholders(
🔴  100 |             mapOf(
🔴  101 |                 "app_role" to config.appRole,
🔴  102 |                 "app_role_password" to config.appRolePassword,
⚪  103 |             )
⚪  104 |         )
🔴  105 |         .load()
🔴  106 |         .migrate()
⚪  107 | }
⚪  108 | 
⚪  109 | private fun requireSecretValue(envVarName: String): String {
🔴  110 |     val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
🔴  111 |     if (directValue != null) {
🔴  112 |         return directValue
⚪  113 |     }
⚪  114 | 
🔴  115 |     val fileEnvVarName = "${envVarName}_FILE"
🔴  116 |     val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
🔴  117 |     if (secretFilePath != null) {
🔴  118 |         val secretValue = File(secretFilePath).readText().trim()
🔴  119 |         require(secretValue.isNotEmpty()) {
🔴  120 |             "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
⚪  121 |         }
🔴  122 |         return secretValue
⚪  123 |     }
⚪  124 | 
🔴  125 |     error(
🔴  126 |         "Missing required environment variable $envVarName. " +
🔴  127 |             "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
⚪  128 |     )
⚪  129 | }
```

## Lines 167-171

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:167-171`

```kotlin
🟢  167 |         authenticate {
🟢  168 |             post("/text-adventures/start") {
🟡  169 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  170 |                 val request = call.receive<StartTextAdventureRequest>()
🟢  171 |                 call.respond(
```

## Lines 178-182

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:178-182`

```kotlin
⚪  178 | 
🟢  179 |             post("/text-adventures/respond") {
🟡  180 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  181 |                 val request = call.receive<ContinueTextAdventureRequest>()
🟢  182 |                 call.respond(
```

## Lines 192-198

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:192-198`

```kotlin
⚪  192 | 
🟢  193 |             get("/text-adventures/{adventureId}/messages") {
🟡  194 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟡  195 |                 val adventureId = requireNotNull(call.parameters["adventureId"]) {
🔴  196 |                     "Missing adventureId path parameter"
⚪  197 |                 }
🟢  198 |                 val response = textAdventureService.getAdventureMessages(adventureId)
```
