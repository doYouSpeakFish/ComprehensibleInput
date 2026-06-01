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

## Lines 133-137

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:133-137`

```kotlin
🟢  133 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  134 |             requestKey { call ->
🟡  135 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  136 |             }
⚪  137 |         }
```

## Lines 139-143

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:139-143`

```kotlin
🟢  139 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  140 |             requestKey { call ->
🟡  141 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  142 |             }
⚪  143 |         }
```

## Lines 145-149

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:145-149`

```kotlin
🟢  145 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  146 |             requestKey { call ->
🟡  147 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  148 |             }
⚪  149 |         }
```
