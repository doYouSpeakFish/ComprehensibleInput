package input.comprehensible.backend.textadventure

import input.comprehensible.backend.AppPrincipal
import input.comprehensible.data.textadventures.sources.remote.ContinueTextAdventureRequest
import input.comprehensible.data.textadventures.sources.remote.StartTextAdventureRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.textAdventureRoutes(textAdventureService: TextAdventureGenerationService) {
    authenticate {
        post("/v1/text-adventures/start") {
            requireNotNull(call.principal<AppPrincipal>())
            val request = call.receive<StartTextAdventureRequest>()
            call.respond(textAdventureService.startAdventure(request.learningLanguage, request.translationsLanguage))
        }
        post("/v1/text-adventures/respond") {
            requireNotNull(call.principal<AppPrincipal>())
            val request = call.receive<ContinueTextAdventureRequest>()
            call.respond(
                textAdventureService.respondToUser(
                    request.adventureId,
                    request.learningLanguage,
                    request.translationsLanguage,
                    request.userMessage,
                    request.history,
                )
            )
        }
        get("/v1/text-adventures/{adventureId}/messages") {
            requireNotNull(call.principal<AppPrincipal>())
            val adventureId = requireNotNull(call.parameters["adventureId"])
            val response = textAdventureService.getAdventureMessages(adventureId)
            if (response == null) call.respond(HttpStatusCode.NotFound) else call.respond(response)
        }
    }
}
