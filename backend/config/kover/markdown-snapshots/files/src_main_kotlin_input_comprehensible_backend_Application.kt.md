# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 47-96

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:47-96`

```kotlin
⚪   47 | 
⚪   48 | fun main() {
🔴   49 |     val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
🔴   50 |     val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
🔴   51 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   52 |         ?.takeIf { it.isNotBlank() }
🔴   53 |         ?: DEFAULT_DATABASE_URL
⚪   54 | 
🔴   55 |     val databaseUser = System.getenv(BACKEND_DATABASE_USER_ENV_VAR)
🔴   56 |         ?.takeIf { it.isNotBlank() }
🔴   57 |         ?: DEFAULT_APP_DATABASE_USER
🔴   58 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
🔴   59 |     val databaseAdminUser = System.getenv(BACKEND_DATABASE_ADMIN_USER_ENV_VAR)
🔴   60 |         ?.takeIf { it.isNotBlank() }
🔴   61 |         ?: DEFAULT_ADMIN_DATABASE_USER
🔴   62 |     val databaseAdminPassword = requireSecretValue(BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR)
⚪   63 | 
🔴   64 |     val database = connectDatabase(
🔴   65 |         DatabaseConnectionConfig(
🔴   66 |             databaseUrl = databaseUrl,
🔴   67 |             databaseUser = databaseUser,
🔴   68 |             databasePassword = databasePassword,
🔴   69 |             migrationDatabaseUser = databaseAdminUser,
🔴   70 |             migrationDatabasePassword = databaseAdminPassword,
🔴   71 |             appRole = databaseUser,
🔴   72 |             appRolePassword = databasePassword,
⚪   73 |         )
⚪   74 |     )
⚪   75 | 
🔴   76 |     embeddedServer(
🔴   77 |         factory = Netty,
🔴   78 |         port = 8080,
🔴   79 |         host = "0.0.0.0",
⚪   80 |         module = {
🔴   81 |             configureRouting(
🔴   82 |                 textAdventureService = TextAdventureGenerationService(
🔴   83 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   84 |                         apiKey = aiApiKey,
⚪   85 |                     ),
🔴   86 |                     adventureRepository = DatabaseAdventureRepository(
🔴   87 |                         database = database,
⚪   88 |                     ),
⚪   89 |                 ),
🔴   90 |                 appApiKey = apiKey,
🔴   91 |                 accountService = AccountService(database),
⚪   92 |             )
⚪   93 |         },
🔴   94 |     ).start(wait = true)
⚪   95 | }
⚪   96 | 
```

## Lines 123-144

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:123-144`

```kotlin
⚪  123 | 
⚪  124 | private fun requireSecretValue(envVarName: String): String {
🔴  125 |     val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
🔴  126 |     if (directValue != null) {
🔴  127 |         return directValue
⚪  128 |     }
⚪  129 | 
🔴  130 |     val fileEnvVarName = "${envVarName}_FILE"
🔴  131 |     val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
🔴  132 |     if (secretFilePath != null) {
🔴  133 |         val secretValue = File(secretFilePath).readText().trim()
🔴  134 |         require(secretValue.isNotEmpty()) {
🔴  135 |             "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
⚪  136 |         }
🔴  137 |         return secretValue
⚪  138 |     }
⚪  139 | 
🔴  140 |     error(
🔴  141 |         "Missing required environment variable $envVarName. " +
🔴  142 |             "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
⚪  143 |     )
⚪  144 | }
```

## Lines 155-159

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:155-159`

```kotlin
🟢  155 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  156 |             requestKey { call ->
🟡  157 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  158 |             }
⚪  159 |         }
```

## Lines 161-165

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:161-165`

```kotlin
🟢  161 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  162 |             requestKey { call ->
🟡  163 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  164 |             }
⚪  165 |         }
```

## Lines 167-171

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:167-171`

```kotlin
🟢  167 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  168 |             requestKey { call ->
🟡  169 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  170 |             }
⚪  171 |         }
```
