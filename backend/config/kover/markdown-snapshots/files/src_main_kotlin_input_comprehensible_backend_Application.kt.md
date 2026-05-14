# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 44-93

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:44-93`

```kotlin
⚪   44 | 
⚪   45 | fun main() {
🔴   46 |     val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
🔴   47 |     val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
🔴   48 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   49 |         ?.takeIf { it.isNotBlank() }
🔴   50 |         ?: DEFAULT_DATABASE_URL
⚪   51 | 
🔴   52 |     val databaseUser = System.getenv(BACKEND_DATABASE_USER_ENV_VAR)
🔴   53 |         ?.takeIf { it.isNotBlank() }
🔴   54 |         ?: DEFAULT_APP_DATABASE_USER
🔴   55 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
🔴   56 |     val databaseAdminUser = System.getenv(BACKEND_DATABASE_ADMIN_USER_ENV_VAR)
🔴   57 |         ?.takeIf { it.isNotBlank() }
🔴   58 |         ?: DEFAULT_ADMIN_DATABASE_USER
🔴   59 |     val databaseAdminPassword = requireSecretValue(BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR)
⚪   60 | 
🔴   61 |     val database = connectDatabase(
🔴   62 |         DatabaseConnectionConfig(
🔴   63 |             databaseUrl = databaseUrl,
🔴   64 |             databaseUser = databaseUser,
🔴   65 |             databasePassword = databasePassword,
🔴   66 |             migrationDatabaseUser = databaseAdminUser,
🔴   67 |             migrationDatabasePassword = databaseAdminPassword,
🔴   68 |             appRole = databaseUser,
🔴   69 |             appRolePassword = databasePassword,
⚪   70 |         )
⚪   71 |     )
⚪   72 | 
🔴   73 |     embeddedServer(
🔴   74 |         factory = Netty,
🔴   75 |         port = 8080,
🔴   76 |         host = "0.0.0.0",
⚪   77 |         module = {
🔴   78 |             configureRouting(
🔴   79 |                 textAdventureService = TextAdventureGenerationService(
🔴   80 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   81 |                         apiKey = aiApiKey,
⚪   82 |                     ),
🔴   83 |                     adventureRepository = DatabaseAdventureRepository(
🔴   84 |                         database = database,
⚪   85 |                     ),
⚪   86 |                 ),
🔴   87 |                 appApiKey = apiKey,
🔴   88 |                 accountService = AccountService(database),
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
