package input.comprehensible.backend.account

import input.comprehensible.backend.AccountService
import input.comprehensible.backend.AccountSessionPrincipal
import input.comprehensible.backend.SignInPayload
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun Route.accountRoutes(accountService: AccountService) {
    post("/v1/users") {
        val request = call.receive<CredentialsRequest>()
        call.respond(accountService.createAccount(request.email, request.password).status)
    }
    post("/v1/auth/sessions") {
        val request = call.receive<CredentialsRequest>()
        val result = accountService.signIn(request.email, request.password)
        if (result.payload == null) call.respond(result.status) else call.respond(result.status, result.payload.toRemote())
    }
    authenticate("account-bearer") {
        get("/v1/me") {
            val principal = call.principal<AccountSessionPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val me = accountService.getMe(principal.accountId) ?: return@get call.respond(HttpStatusCode.Unauthorized)
            call.respond(HttpStatusCode.OK, me)
        }
        patch("/v1/me") {
            val principal = call.principal<AccountSessionPrincipal>() ?: return@patch call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<UpdateMeRequest>()
            val result = accountService.updateMe(principal.accountId, principal.account.email, request.email, request.password)
            if (result.payload == null) call.respond(result.status) else call.respond(result.status, result.payload)
        }
        delete("/v1/me") {
            val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<DeleteMeRequest>()
            call.respond(accountService.deleteMe(principal.accountId, request.password))
        }
        delete("/v1/auth/sessions/current") {
            val principal = call.principal<AccountSessionPrincipal>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            call.respond(accountService.signOutCurrent(principal.token))
        }
    }
}

@Serializable data class CredentialsRequest(val email: String, val password: String)
@Serializable data class UpdateMeRequest(val email: String? = null, val password: String? = null)
@Serializable data class DeleteMeRequest(val password: String? = null)

@Serializable
data class SignInRemoteResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
)

private fun SignInPayload.toRemote() = SignInRemoteResponse(accessToken, tokenType)
