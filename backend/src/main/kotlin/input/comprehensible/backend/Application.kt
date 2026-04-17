package input.comprehensible.backend

import input.comprehensible.backend.textadventure.DefaultTextAdventureStructuredPromptExecutor
import input.comprehensible.backend.textadventure.TextAdventureGenerationService
import input.comprehensible.data.textadventures.sources.remote.ContinueTextAdventureRequest
import input.comprehensible.data.textadventures.sources.remote.StartTextAdventureRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
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
import kotlin.time.Duration.Companion.minutes

fun main() {
    val apiKey = requireNotNull(System.getenv(AI_API_KEY_ENV_VAR)?.takeIf { it.isNotBlank() }) {
        "Missing required environment variable $AI_API_KEY_ENV_VAR. " +
                "Set it before starting the backend."
    }

    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = {
            configureRouting(
                textAdventureService = TextAdventureGenerationService(
                    structuredPromptExecutor = DefaultTextAdventureStructuredPromptExecutor(
                        apiKey = apiKey,
                    )
                )
            )
        },
    ).start(wait = true)
}

fun Application.configureRouting(
    textAdventureService: TextAdventureGenerationService,
) {
    install(ContentNegotiation) {
        json()
    }
    install(RateLimit) {
        global {
            rateLimiter(limit = 20, refillPeriod = 10.minutes)
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

        post("/text-adventures/start") {
            val request = call.receive<StartTextAdventureRequest>()
            call.respond(
                textAdventureService.startAdventure(
                    learningLanguage = request.learningLanguage,
                    translationsLanguage = request.translationsLanguage,
                )
            )
        }

        post("/text-adventures/respond") {
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
    }
}

private const val AI_API_KEY_ENV_VAR = "KOOG_API_KEY"
