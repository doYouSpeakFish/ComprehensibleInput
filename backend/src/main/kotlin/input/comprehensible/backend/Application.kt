package input.comprehensible.backend

import input.comprehensible.backend.textadventure.DatabaseAdventureRepository
import input.comprehensible.backend.textadventure.DefaultTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.data.textadventures.sources.remote.ContinueTextAdventureRequest
import input.comprehensible.data.textadventures.sources.remote.StartTextAdventureRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.apikey.apiKey
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File
import kotlin.time.Duration.Companion.minutes
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

data class AppPrincipal(val key: String)

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

    migrateDatabase(
        config = MigrationConfig(
            databaseUrl = databaseUrl,
            migrationDatabaseUser = databaseAdminUser,
            migrationDatabasePassword = databaseAdminPassword,
            appRole = databaseUser,
            appRolePassword = databasePassword,
        )
    )

    val database = Database.connect(
        url = databaseUrl,
        driver = POSTGRESQL_JDBC_DRIVER,
        user = databaseUser,
        password = databasePassword,
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
            )
        },
    ).start(wait = true)
}


private data class MigrationConfig(
    val databaseUrl: String,
    val migrationDatabaseUser: String,
    val migrationDatabasePassword: String,
    val appRole: String,
    val appRolePassword: String,
)

private fun migrateDatabase(config: MigrationConfig) {
    Flyway.configure()
        .dataSource(config.databaseUrl, config.migrationDatabaseUser, config.migrationDatabasePassword)
        .placeholders(
            mapOf(
                "app_role" to config.appRole,
                "app_role_password" to config.appRolePassword,
            )
        )
        .load()
        .migrate()
}

private fun requireSecretValue(envVarName: String): String {
    val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
    if (directValue != null) {
        return directValue
    }

    val fileEnvVarName = "${envVarName}_FILE"
    val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
    if (secretFilePath != null) {
        val secretValue = File(secretFilePath).readText().trim()
        require(secretValue.isNotEmpty()) {
            "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
        }
        return secretValue
    }

    error(
        "Missing required environment variable $envVarName. " +
            "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
    )
}

fun Application.configureRouting(
    textAdventureService: TextAdventureGenerationService,
    appApiKey: String,
) {
    install(ContentNegotiation) {
        json()
    }
    install(RateLimit) {
        global {
            rateLimiter(limit = 20, refillPeriod = 10.minutes)
        }
    }
    install(Authentication) {
        apiKey {
            validate { keyFromHeader ->
                keyFromHeader
                    .takeIf { it == appApiKey }
                    ?.let { AppPrincipal(it) }
            }
            challenge { call ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Invalid or missing API key"
                )
            }
        }
    }
    routing {
        get("/health") {
            call.respondText(
                text = "ok",
                contentType = ContentType.Text.Plain,
                status = HttpStatusCode.OK,
            )
        }

        authenticate {
            post("/text-adventures/start") {
                requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
                val request = call.receive<StartTextAdventureRequest>()
                call.respond(
                    textAdventureService.startAdventure(
                        learningLanguage = request.learningLanguage,
                        translationsLanguage = request.translationsLanguage,
                    )
                )
            }

            post("/text-adventures/respond") {
                requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
                val request = call.receive<ContinueTextAdventureRequest>()
                call.respond(
                    textAdventureService.respondToUser(
                        adventureId = request.adventureId,
                        learningLanguage = request.learningLanguage,
                        translationsLanguage = request.translationsLanguage,
                        userMessage = request.userMessage,
                        history = request.history,
                    )
                )
            }

            get("/text-adventures/{adventureId}/messages") {
                requireNotNull(call.principal<AppPrincipal>()) { "Unauthenticated" }
                val adventureId = requireNotNull(call.parameters["adventureId"]) {
                    "Missing adventureId path parameter"
                }
                val response = textAdventureService.getAdventureMessages(adventureId)
                if (response == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(response)
                }
            }
        }
    }
}

private const val AI_API_KEY_ENV_VAR = "KOOG_API_KEY"
private const val APP_API_KEY_ENV_VAR = "APP_API_KEY"
private const val BACKEND_DATABASE_URL_ENV_VAR = "BACKEND_DATABASE_URL"
private const val BACKEND_DATABASE_USER_ENV_VAR = "BACKEND_DATABASE_USER"
private const val BACKEND_DATABASE_PASSWORD_ENV_VAR = "BACKEND_DATABASE_PASSWORD"
private const val BACKEND_DATABASE_ADMIN_USER_ENV_VAR = "BACKEND_DATABASE_ADMIN_USER"
private const val BACKEND_DATABASE_ADMIN_PASSWORD_ENV_VAR = "BACKEND_DATABASE_ADMIN_PASSWORD"
private const val DEFAULT_DATABASE_URL = "jdbc:postgresql://localhost:5432/comprehensible_input"
private const val DEFAULT_APP_DATABASE_USER = "comprehensible_app"
private const val DEFAULT_ADMIN_DATABASE_USER = "comprehensible_admin"
private const val POSTGRESQL_JDBC_DRIVER = "org.postgresql.Driver"
