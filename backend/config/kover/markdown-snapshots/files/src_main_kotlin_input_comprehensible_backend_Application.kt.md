# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 28-82

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:28-82`

```kotlin
⚪   28 | import org.jetbrains.exposed.sql.Database
⚪   29 | 
🔴   30 | data class AppPrincipal(val key: String)
⚪   31 | 
⚪   32 | fun main() {
🔴   33 |     val aiApiKey = requireNotNull(System.getenv(AI_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   34 |         "Missing required environment variable $AI_API_KEY_ENV_VAR. " +
🔴   35 |             "Set it before starting the backend."
⚪   36 |     }
🔴   37 |     val apiKey = requireNotNull(System.getenv(APP_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   38 |         "Missing required environment variable $APP_API_KEY_ENV_VAR. " +
🔴   39 |             "Set it before starting the backend."
⚪   40 |     }
🔴   41 |     val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
🔴   42 |         ?.takeIf { it.isNotBlank() }
🔴   43 |         ?: DEFAULT_DATABASE_URL
⚪   44 | 
🔴   45 |     val databaseUser = requireNotNull(System.getenv(BACKEND_DATABASE_USER_ENV_VAR)?.takeIf { it.isNotBlank() }) {
🔴   46 |         "Missing required environment variable $BACKEND_DATABASE_USER_ENV_VAR. " +
🔴   47 |             "Set it before starting the backend."
⚪   48 |     }
🔴   49 |     val databasePassword = requireNotNull(
🔴   50 |         System.getenv(BACKEND_DATABASE_PASSWORD_ENV_VAR)?.takeIf { it.isNotBlank() }
⚪   51 |     ) {
🔴   52 |         "Missing required environment variable $BACKEND_DATABASE_PASSWORD_ENV_VAR. " +
🔴   53 |             "Set it before starting the backend."
⚪   54 |     }
⚪   55 | 
🔴   56 |     val database = Database.connect(
🔴   57 |         url = databaseUrl,
🔴   58 |         driver = MYSQL_JDBC_DRIVER,
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

## Lines 85-156

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:85-156`

```kotlin
⚪   85 |     appApiKey: String,
⚪   86 | ) {
🔴   87 |     install(ContentNegotiation) {
🔴   88 |         json()
⚪   89 |     }
🔴   90 |     install(RateLimit) {
🔴   91 |         global {
🔴   92 |             rateLimiter(limit = 20, refillPeriod = 10.minutes)
⚪   93 |         }
⚪   94 |     }
🔴   95 |     install(Authentication) {
🔴   96 |         apiKey {
🔴   97 |             validate { keyFromHeader ->
🔴   98 |                 keyFromHeader
🔴   99 |                     .takeIf { it == appApiKey }
🔴  100 |                     ?.let { AppPrincipal(it) }
⚪  101 |             }
🔴  102 |             challenge { call ->
🔴  103 |                 call.respond(
🔴  104 |                     HttpStatusCode.Unauthorized,
🔴  105 |                     "Invalid or missing API key"
⚪  106 |                 )
⚪  107 |             }
⚪  108 |         }
⚪  109 |     }
🔴  110 |     routing {
🔴  111 |         get("/health") {
🔴  112 |             call.respondText(
🔴  113 |                 text = "ok",
🔴  114 |                 contentType = ContentType.Text.Plain,
🔴  115 |                 status = HttpStatusCode.OK,
⚪  116 |             )
⚪  117 |         }
⚪  118 | 
🔴  119 |         authenticate {
🔴  120 |             post("/text-adventures/start") {
🔴  121 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🔴  122 |                 val request = call.receive<StartTextAdventureRequest>()
🔴  123 |                 call.respond(
🔴  124 |                     textAdventureService.startAdventure(
🔴  125 |                         learningLanguage = request.learningLanguage,
🔴  126 |                         translationsLanguage = request.translationsLanguage,
⚪  127 |                     )
⚪  128 |                 )
⚪  129 |             }
⚪  130 | 
🔴  131 |             post("/text-adventures/respond") {
🔴  132 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🔴  133 |                 val request = call.receive<ContinueTextAdventureRequest>()
🔴  134 |                 call.respond(
🔴  135 |                     textAdventureService.respondToUser(
🔴  136 |                         adventureId = request.adventureId,
🔴  137 |                         learningLanguage = request.learningLanguage,
🔴  138 |                         translationsLanguage = request.translationsLanguage,
🔴  139 |                         userMessage = request.userMessage,
🔴  140 |                         history = request.history,
⚪  141 |                     )
⚪  142 |                 )
⚪  143 |             }
⚪  144 | 
🔴  145 |             get("/text-adventures/{adventureId}/messages") {
🔴  146 |                 requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
🔴  147 |                 val adventureId = requireNotNull(call.parameters["adventureId"]) {
🔴  148 |                     "Missing adventureId path parameter"
⚪  149 |                 }
🔴  150 |                 val response = textAdventureService.getAdventureMessages(adventureId)
🔴  151 |                 if (response == null) {
🔴  152 |                     call.respond(HttpStatusCode.NotFound)
⚪  153 |                 } else {
🔴  154 |                     call.respond(response)
⚪  155 |                 }
⚪  156 |             }
```
