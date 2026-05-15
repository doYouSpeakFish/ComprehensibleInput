# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 46-95

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:46-95`

```kotlin
⚪   46 | 
⚪   47 | fun main() {
🔴   48 |     val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
🔴   49 |     val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
🔴   50 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   51 |         ?.takeIf { it.isNotBlank() }
🔴   52 |         ?: DEFAULT_DATABASE_URL
⚪   53 | 
🔴   54 |     val databaseUser = System.getenv(BACKEND_DATABASE_USER_ENV_VAR)
🔴   55 |         ?.takeIf { it.isNotBlank() }
🔴   56 |         ?: DEFAULT_APP_DATABASE_USER
🔴   57 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
🔴   58 |     val databaseAdminUser = System.getenv(BACKEND_DATABASE_ADMIN_USER_ENV_VAR)
🔴   59 |         ?.takeIf { it.isNotBlank() }
🔴   60 |         ?: DEFAULT_ADMIN_DATABASE_USER
🔴   61 |     val databaseAdminPassword = requireSecretValue(BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR)
⚪   62 | 
🔴   63 |     val database = connectDatabase(
🔴   64 |         DatabaseConnectionConfig(
🔴   65 |             databaseUrl = databaseUrl,
🔴   66 |             databaseUser = databaseUser,
🔴   67 |             databasePassword = databasePassword,
🔴   68 |             migrationDatabaseUser = databaseAdminUser,
🔴   69 |             migrationDatabasePassword = databaseAdminPassword,
🔴   70 |             appRole = databaseUser,
🔴   71 |             appRolePassword = databasePassword,
⚪   72 |         )
⚪   73 |     )
⚪   74 | 
🔴   75 |     embeddedServer(
🔴   76 |         factory = Netty,
🔴   77 |         port = 8080,
🔴   78 |         host = "0.0.0.0",
⚪   79 |         module = {
🔴   80 |             configureRouting(
🔴   81 |                 textAdventureService = TextAdventureGenerationService(
🔴   82 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   83 |                         apiKey = aiApiKey,
⚪   84 |                     ),
🔴   85 |                     adventureRepository = DatabaseAdventureRepository(
🔴   86 |                         database = database,
⚪   87 |                     ),
⚪   88 |                 ),
🔴   89 |                 appApiKey = apiKey,
🔴   90 |                 accountService = AccountService(database),
⚪   91 |             )
⚪   92 |         },
🔴   93 |     ).start(wait = true)
⚪   94 | }
⚪   95 | 
```

## Lines 122-143

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:122-143`

```kotlin
⚪  122 | 
⚪  123 | private fun requireSecretValue(envVarName: String): String {
🔴  124 |     val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
🔴  125 |     if (directValue != null) {
🔴  126 |         return directValue
⚪  127 |     }
⚪  128 | 
🔴  129 |     val fileEnvVarName = "${envVarName}_FILE"
🔴  130 |     val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
🔴  131 |     if (secretFilePath != null) {
🔴  132 |         val secretValue = File(secretFilePath).readText().trim()
🔴  133 |         require(secretValue.isNotEmpty()) {
🔴  134 |             "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
⚪  135 |         }
🔴  136 |         return secretValue
⚪  137 |     }
⚪  138 | 
🔴  139 |     error(
🔴  140 |         "Missing required environment variable $envVarName. " +
🔴  141 |             "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
⚪  142 |     )
⚪  143 | }
```

## Lines 154-158

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:154-158`

```kotlin
🟢  154 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  155 |             requestKey { call ->
🟡  156 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  157 |             }
⚪  158 |         }
```

## Lines 160-164

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:160-164`

```kotlin
🟢  160 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  161 |             requestKey { call ->
🟡  162 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  163 |             }
⚪  164 |         }
```

## Lines 166-170

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:166-170`

```kotlin
🟢  166 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  167 |             requestKey { call ->
🟡  168 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  169 |             }
⚪  170 |         }
```
