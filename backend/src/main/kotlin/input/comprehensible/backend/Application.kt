package input.comprehensible.backend

import input.comprehensible.backend.common.requireSecretValue
import input.comprehensible.backend.textadventure.ADVENTURE_IMAGES_PATH
import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.DefaultTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.backend.textadventure.textAdventureRoutes
import input.comprehensible.backend.textadventure.textAdventureV1Routes
import input.comprehensible.backend.account.accountRoutes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.apikey.apiKey
import io.ktor.server.auth.bearer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receiveText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


data class AppPrincipal(val key: String)
data class AccountSessionPrincipal(val token: String, val accountId: String, val account: AccountPayload)

data class DatabaseConnectionConfig(
    val databaseUrl: String,
    val databaseUser: String,
    val databasePassword: String,
    val migrationDatabaseUser: String,
    val migrationDatabasePassword: String,
    val appRole: String,
    val appRolePassword: String,
    val flywayLocations: List<String> = listOf("classpath:db/migration"),
)


fun main() {
    val aiApiKey = requireSecretValue(AI_API_KEY_ENV_VAR)
    val apiKey = requireSecretValue(APP_API_KEY_ENV_VAR)
    val databaseUrl = System.getenv(BACKEND_DATABASE_URL_ENV_VAR)
        ?.takeIf { it.isNotBlank() }
        ?: DEFAULT_DATABASE_URL

    val databaseUser = System.getenv(BACKEND_DATABASE_USER_ENV_VAR)
        ?.takeIf { it.isNotBlank() }
        ?: DEFAULT_APP_DATABASE_USER
    val databasePassword = requireSecretValue(BACKEND_DATABASE_PASSWORD_ENV_VAR)
    val databaseAdminUser = System.getenv(BACKEND_DATABASE_ADMIN_USER_ENV_VAR)
        ?.takeIf { it.isNotBlank() }
        ?: DEFAULT_ADMIN_DATABASE_USER
    val databaseAdminPassword = requireSecretValue(BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR)

    val database = connectDatabase(
        DatabaseConnectionConfig(
            databaseUrl = databaseUrl,
            databaseUser = databaseUser,
            databasePassword = databasePassword,
            migrationDatabaseUser = databaseAdminUser,
            migrationDatabasePassword = databaseAdminPassword,
            appRole = databaseUser,
            appRolePassword = databasePassword,
        )
    )

    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = {
            configureRouting(
                textAdventureService = TextAdventureGenerationService(
                    structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
                        apiKey = aiApiKey,
                    ),
                    adventureRepository = DatabaseAdventureRepository(
                        database = database,
                    ),
                ),
                appApiKey = apiKey,
                accountService = AccountService(database),
            )
        },
    ).start(wait = true)
}


fun connectDatabase(config: DatabaseConnectionConfig): Database {
    migrateDatabase(config)

    return Database.connect(
        url = config.databaseUrl,
        driver = POSTGRESQL_JDBC_DRIVER,
        user = config.databaseUser,
        password = config.databasePassword,
    )
}

@Suppress("SpreadOperator")
private fun migrateDatabase(config: DatabaseConnectionConfig) {
    Flyway.configure()
        .dataSource(config.databaseUrl, config.migrationDatabaseUser, config.migrationDatabasePassword)
        .locations(*config.flywayLocations.toTypedArray())
        .placeholders(
            mapOf(
                "app_role" to config.appRole,
                "app_role_password" to config.appRolePassword,
            )
        )
        .load()
        .migrate()
}


private fun Application.configureRateLimits(globalRateLimit: Int) {
    install(RateLimit) {
        global {
            // A general flood / brute-force baseline applied to every route, keyed per client IP so a
            // single source cannot use up everyone else's allowance. The text adventure feature now
            // carries its own (low, AI-cost-driven) limit, so this catch-all only needs to stop
            // egregious abuse and can therefore be far more generous.
            rateLimiter(limit = globalRateLimit, refillPeriod = 10.minutes)
            requestKey { call -> call.request.local.remoteHost }
        }
        register(RateLimitName(TEXT_ADVENTURE_RATE_LIMIT_NAME)) {
            rateLimiter(limit = TEXT_ADVENTURE_RATE_LIMIT, refillPeriod = 10.minutes)
            // A constant key applies the limit to the text adventure feature as a whole, shared
            // across all users, rather than giving each user (or IP) their own separate allowance.
            requestKey { TEXT_ADVENTURE_RATE_LIMIT_KEY }
        }
        register(RateLimitName("email-verification")) {
            rateLimiter(limit = 1, refillPeriod = 30.seconds)
            requestKey { call -> emailFromQueryParamOrIp(call) }
        }
        register(RateLimitName("password-reset-request")) {
            rateLimiter(limit = 1, refillPeriod = 30.seconds)
            requestKey { call -> emailFromQueryParamOrIp(call) }
        }
        register(RateLimitName("password-reset-attempt")) {
            rateLimiter(limit = 1, refillPeriod = 30.seconds)
            requestKey { call -> emailFromQueryParamOrIp(call) }
        }
        register(RateLimitName("email-verification-code")) {
            rateLimiter(limit = 1, refillPeriod = 30.seconds)
            requestKey { call -> emailFromBodyOrIp(call) }
        }
        register(RateLimitName("email-change-current-verification-code")) {
            rateLimiter(limit = 1, refillPeriod = 30.seconds)
            requestKey { call -> tokenOrIp(call) }
        }
        register(RateLimitName("email-change-new-verification-code")) {
            rateLimiter(limit = 1, refillPeriod = 30.seconds)
            requestKey { call -> tokenOrIp(call) }
        }
        register(RateLimitName("account-deletion")) {
            rateLimiter(limit = 1, refillPeriod = 30.seconds)
            requestKey { call -> emailFromBodyOrIp(call) }
        }
    }
}

private fun emailFromQueryParamOrIp(call: io.ktor.server.application.ApplicationCall): String =
    call.request.queryParameters["email"]
        ?: call.request.local.remoteHost

private suspend fun emailFromBodyOrIp(call: io.ktor.server.application.ApplicationCall): String =
    runCatching {
        Json.parseToJsonElement(call.receiveText()).jsonObject["email"]?.jsonPrimitive?.content
    }.getOrNull() ?: call.request.local.remoteHost

private fun tokenOrIp(call: io.ktor.server.application.ApplicationCall): String =
    call.request.headers["Authorization"]
        ?: call.request.local.remoteHost

fun Application.configureRouting(
    textAdventureService: TextAdventureGenerationService,
    appApiKey: String,
    accountService: AccountService,
    globalRateLimit: Int = GLOBAL_RATE_LIMIT,
) {
    install(ContentNegotiation) { json() }
    install(DoubleReceive)
    configureRateLimits(globalRateLimit)
    install(Authentication) {
        apiKey {
            validate { keyFromHeader -> keyFromHeader.takeIf { it == appApiKey }?.let { AppPrincipal(it) } }
            challenge { call -> call.respond(HttpStatusCode.Unauthorized, "Invalid or missing API key") }
        }
        bearer("account-bearer") {
            authenticate { credential ->
                accountService.findAccountBySessionToken(credential.token)
            }
        }
    }
    routing {
        get("/health") { call.respondText("ok", ContentType.Text.Plain, HttpStatusCode.OK) }
        // Adventure cover images are public, cacheable static assets served straight from the
        // bundled resources (loaded by the app with Coil). They carry no user data, so they sit
        // outside authentication and the text adventure rate limit.
        staticResources("/$ADVENTURE_IMAGES_PATH", ADVENTURE_IMAGES_PATH)
        accountRoutes(accountService)
        rateLimit(RateLimitName(TEXT_ADVENTURE_RATE_LIMIT_NAME)) {
            textAdventureRoutes(textAdventureService)
            textAdventureV1Routes(textAdventureService)
        }
    }
}

/**
 * The text adventure feature is rate limited during early access. The limit is named (rather than
 * global) so it only applies to the text adventure routes, and it uses a constant request key so the
 * allowance is shared across all users instead of being tracked per user.
 */
internal const val TEXT_ADVENTURE_RATE_LIMIT_NAME = "text-adventure"
internal const val TEXT_ADVENTURE_RATE_LIMIT = 20
private const val TEXT_ADVENTURE_RATE_LIMIT_KEY = "text-adventure"

/**
 * General baseline rate limit applied to every route, per client IP, over 10 minutes (flood /
 * brute-force protection). It is far more generous than the text adventure limit: that one is kept
 * low only to cap AI usage costs, whereas this catch-all just needs to stop egregious abuse from a
 * single source. 1000 requests per IP per 10 minutes is already well above normal use.
 */
internal const val GLOBAL_RATE_LIMIT = 1_000

private const val AI_API_KEY_ENV_VAR = "KOOG_API_KEY"
private const val APP_API_KEY_ENV_VAR = "APP_API_KEY"
private const val BACKEND_DATABASE_URL_ENV_VAR = "BACKEND_DATABASE_URL"
private const val BACKEND_DATABASE_USER_ENV_VAR = "BACKEND_DATABASE_USER"
private const val BACKEND_DATABASE_PASSWORD_ENV_VAR = "BACKEND_DATABASE_PASSWORD"
private const val BACKEND_DATABASE_ADMIN_USER_ENV_VAR = "BACKEND_DATABASE_ADMIN_USER"
private const val BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR = "BACKEND_DATABASE_ADMIN_PASSWORD"
private const val DEFAULT_DATABASE_URL = "jdbc:postgresql://localhost:5432/comprehensible_input"
private const val DEFAULT_APP_DATABASE_USER = "app"
private const val DEFAULT_ADMIN_DATABASE_USER = "admin"
private const val POSTGRESQL_JDBC_DRIVER = "org.postgresql.Driver"
