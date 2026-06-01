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

## Lines 134-138

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:134-138`

```kotlin
🟢  134 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  135 |             requestKey { call ->
🟡  136 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  137 |             }
⚪  138 |         }
```

## Lines 140-144

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:140-144`

```kotlin
🟢  140 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  141 |             requestKey { call ->
🟡  142 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  143 |             }
⚪  144 |         }
```

## Lines 146-150

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:146-150`

```kotlin
🟢  146 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  147 |             requestKey { call ->
🟡  148 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  149 |             }
⚪  150 |         }
```
