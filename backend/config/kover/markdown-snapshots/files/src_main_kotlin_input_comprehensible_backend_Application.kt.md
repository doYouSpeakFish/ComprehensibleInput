# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 52-101

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:52-101`

```kotlin
⚪   52 | 
⚪   53 | fun main() {
🔴   54 |     val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
🔴   55 |     val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
🔴   56 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   57 |         ?.takeIf { it.isNotBlank() }
🔴   58 |         ?: DEFAULT_DATABASE_URL
⚪   59 | 
🔴   60 |     val databaseUser = System.getenv(BACKEND_DATABASE_USER_ENV_VAR)
🔴   61 |         ?.takeIf { it.isNotBlank() }
🔴   62 |         ?: DEFAULT_APP_DATABASE_USER
🔴   63 |     val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
🔴   64 |     val databaseAdminUser = System.getenv(BACKEND_DATABASE_ADMIN_USER_ENV_VAR)
🔴   65 |         ?.takeIf { it.isNotBlank() }
🔴   66 |         ?: DEFAULT_ADMIN_DATABASE_USER
🔴   67 |     val databaseAdminPassword = requireSecretValue(BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR)
⚪   68 | 
🔴   69 |     val database = connectDatabase(
🔴   70 |         DatabaseConnectionConfig(
🔴   71 |             databaseUrl = databaseUrl,
🔴   72 |             databaseUser = databaseUser,
🔴   73 |             databasePassword = databasePassword,
🔴   74 |             migrationDatabaseUser = databaseAdminUser,
🔴   75 |             migrationDatabasePassword = databaseAdminPassword,
🔴   76 |             appRole = databaseUser,
🔴   77 |             appRolePassword = databasePassword,
⚪   78 |         )
⚪   79 |     )
⚪   80 | 
🔴   81 |     embeddedServer(
🔴   82 |         factory = Netty,
🔴   83 |         port = 8080,
🔴   84 |         host = "0.0.0.0",
⚪   85 |         module = {
🔴   86 |             configureRouting(
🔴   87 |                 textAdventureService = TextAdventureGenerationService(
🔴   88 |                     structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
🔴   89 |                         apiKey = aiApiKey,
⚪   90 |                     ),
🔴   91 |                     adventureRepository = DatabaseAdventureRepository(
🔴   92 |                         database = database,
⚪   93 |                     ),
⚪   94 |                 ),
🔴   95 |                 appApiKey = apiKey,
🔴   96 |                 accountService = AccountService(database),
⚪   97 |             )
⚪   98 |         },
🔴   99 |     ).start(wait = true)
⚪  100 | }
⚪  101 | 
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

## Lines 152-156

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:152-156`

```kotlin
🟢  152 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  153 |             requestKey { call ->
🟡  154 |                 call.request.queryParameters["email"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  155 |             }
⚪  156 |         }
```

## Lines 159-166

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:159-166`

```kotlin
🟢  159 |             requestKey { call ->
⚪  160 |                 try {
🟡  161 |                     Json.parseToJsonElement(call.receiveText()).jsonObject["email"]?.jsonPrimitive?.content
🔴  162 |                         ?: (call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost)
⚪  163 |                 } catch (e: Exception) {
🔴  164 |                     call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  165 |                 }
⚪  166 |             }
```

## Lines 169-173

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:169-173`

```kotlin
🟢  169 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  170 |             requestKey { call ->
🟡  171 |                 call.request.headers["Authorization"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  172 |             }
⚪  173 |         }
```

## Lines 175-179

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:175-179`

```kotlin
🟢  175 |             rateLimiter(limit = 1, refillPeriod = 30.seconds)
🟢  176 |             requestKey { call ->
🟡  177 |                 call.request.headers["Authorization"] ?: call.request.headers["X-Forwarded-For"] ?: call.request.local.remoteHost
⚪  178 |             }
⚪  179 |         }
```
