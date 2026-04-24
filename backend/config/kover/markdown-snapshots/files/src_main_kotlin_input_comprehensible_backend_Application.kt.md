# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 33-131

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:33-131`

```kotlin
⚪   33 | 
⚪   34 | fun main() {
🔴   35 |     val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
🔴   36 |     val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
🔴   37 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   38 |         ?.takeIf { it.isNotBlank() }
🔴   39 |         ?: DEFAULT_DATABASE_URL
⚪   40 | 
🔴   41 |     val databaseUser = requireSecretValue(BACKEND_DATABASE_USER_ENV_VAR)
🔴   42 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
🔴   43 |     val migrationDatabaseUser = requireSecretValue(BACKEND_MIGRATION_DATABASE_USER_ENV_VAR)
🔴   44 |     val migrationDatabasePassword = requireSecretValue(BACKEND_MIGRATION_DATABASE_PASSWORD_ENV_VAR)
⚪   45 | 
🔴   46 |     migrateDatabase(
🔴   47 |         config = MigrationConfig(
🔴   48 |             databaseUrl = databaseUrl,
🔴   49 |             migrationDatabaseUser = migrationDatabaseUser,
🔴   50 |             migrationDatabasePassword = migrationDatabasePassword,
🔴   51 |             migrationRole = migrationDatabaseUser,
🔴   52 |             migrationRolePassword = migrationDatabasePassword,
🔴   53 |             appRole = databaseUser,
🔴   54 |             appRolePassword = databasePassword,
⚪   55 |         )
⚪   56 |     )
⚪   57 | 
🔴   58 |     val database = Database.connect(
🔴   59 |         url = databaseUrl,
🔴   60 |         driver = POSTGRESQL_JDBC_DRIVER,
🔴   61 |         user = databaseUser,
🔴   62 |         password = databasePassword,
⚪   63 |     )
⚪   64 | 
🔴   65 |     embeddedServer(
🔴   66 |         factory = Netty,
🔴   67 |         port = 8080,
🔴   68 |         host = "0.0.0.0",
⚪   69 |         module = {
🔴   70 |             configureRouting(
🔴   71 |                 textAdventureService = TextAdventureGenerationService(
🔴   72 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   73 |                         apiKey = aiApiKey,
⚪   74 |                     ),
🔴   75 |                     adventureRepository = DatabaseAdventureRepository(
🔴   76 |                         database = database,
⚪   77 |                     ),
⚪   78 |                 ),
🔴   79 |                 appApiKey = apiKey,
⚪   80 |             )
⚪   81 |         },
🔴   82 |     ).start(wait = true)
⚪   83 | }
⚪   84 | 
⚪   85 | 
🔴   86 | private data class MigrationConfig(
🔴   87 |     val databaseUrl: String,
🔴   88 |     val migrationDatabaseUser: String,
🔴   89 |     val migrationDatabasePassword: String,
🔴   90 |     val migrationRole: String,
🔴   91 |     val migrationRolePassword: String,
🔴   92 |     val appRole: String,
🔴   93 |     val appRolePassword: String,
⚪   94 | )
⚪   95 | 
⚪   96 | private fun migrateDatabase(config: MigrationConfig) {
🔴   97 |     Flyway.configure()
🔴   98 |         .dataSource(config.databaseUrl, config.migrationDatabaseUser, config.migrationDatabasePassword)
🔴   99 |         .placeholders(
🔴  100 |             mapOf(
🔴  101 |                 "migration_role" to config.migrationRole,
🔴  102 |                 "migration_role_password" to config.migrationRolePassword,
🔴  103 |                 "app_role" to config.appRole,
🔴  104 |                 "app_role_password" to config.appRolePassword,
⚪  105 |             )
⚪  106 |         )
🔴  107 |         .load()
🔴  108 |         .migrate()
⚪  109 | }
⚪  110 | 
⚪  111 | private fun requireSecretValue(envVarName: String): String {
🔴  112 |     val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
🔴  113 |     if (directValue != null) {
🔴  114 |         return directValue
⚪  115 |     }
⚪  116 | 
🔴  117 |     val fileEnvVarName = "${envVarName}_FILE"
🔴  118 |     val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
🔴  119 |     if (secretFilePath != null) {
🔴  120 |         val secretValue = File(secretFilePath).readText().trim()
🔴  121 |         require(secretValue.isNotEmpty()) {
🔴  122 |             "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
⚪  123 |         }
🔴  124 |         return secretValue
⚪  125 |     }
⚪  126 | 
🔴  127 |     error(
🔴  128 |         "Missing required environment variable $envVarName. " +
🔴  129 |             "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
⚪  130 |     )
⚪  131 | }
```

## Lines 169-173

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:169-173`

```kotlin
🟢  169 |         authenticate {
🟢  170 |             post("/text-adventures/start") {
🟡  171 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  172 |                 val request = call.receive<StartTextAdventureRequest>()
🟢  173 |                 call.respond(
```

## Lines 180-184

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:180-184`

```kotlin
⚪  180 | 
🟢  181 |             post("/text-adventures/respond") {
🟡  182 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟢  183 |                 val request = call.receive<ContinueTextAdventureRequest>()
🟢  184 |                 call.respond(
```

## Lines 194-200

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:194-200`

```kotlin
⚪  194 | 
🟢  195 |             get("/text-adventures/{adventureId}/messages") {
🟡  196 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🟡  197 |                 val adventureId = requireNotNull(call.parameters["adventureId"]) {
🔴  198 |                     "Missing adventureId path parameter"
⚪  199 |                 }
🟢  200 |                 val response = textAdventureService.getAdventureMessages(adventureId)
```
