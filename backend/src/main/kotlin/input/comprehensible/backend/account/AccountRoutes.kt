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
import io.ktor.server.plugins.ratelimit.rateLimit
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
    rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("email-verification")) {
        post("/v1/email-verifications") {
            val request = call.receive<EmailVerificationRequest>()
            call.respond(accountService.verifyEmail(request.email, request.code))
        }
    }
    rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("email-verification-code")) {
        post("/v1/email-verification-codes") {
            val request = call.receive<EmailVerificationCodeRequest>()
            call.respond(accountService.requestNewEmailVerificationCode(request.email))
        }
    }
    rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("password-reset-request")) {
        post("/v1/password-reset-codes") {
            val request = call.receive<PasswordResetCodeRequest>()
            call.respond(accountService.requestPasswordReset(request.email))
        }
    }
    rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("password-reset-attempt")) {
        post("/v1/password-resets") {
            val request = call.receive<PasswordResetRequest>()
            call.respond(accountService.resetPassword(request.email, request.password, request.code))
        }
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
            call.respond(accountService.updateMe(accountId = principal.accountId, newEmail = request.email, password = request.password))
        }
        post("/v1/email-change-current-verifications") {
            val principal = call.principal<AccountSessionPrincipal>()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<EmailChangeCurrentVerificationRequest>()
            call.respond(accountService.verifyCurrentEmailChange(accountId = principal.accountId, code = request.code))
        }
        rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("email-change-current-verification-code")) {
            post("/v1/email-change-current-verification-codes") {
                val principal = call.principal<AccountSessionPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                call.respond(accountService.requestNewEmailChangeCurrentCode(principal.accountId))
            }
        }
        post("/v1/email-change-verifications") {
            val principal = call.principal<AccountSessionPrincipal>()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<EmailVerificationRequest>()
            call.respond(accountService.verifyPendingEmailChange(principal.accountId, request.email, request.code))
        }
        rateLimit(io.ktor.server.plugins.ratelimit.RateLimitName("email-change-new-verification-code")) {
            post("/v1/email-change-new-verification-codes") {
                val principal = call.principal<AccountSessionPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                call.respond(accountService.requestNewEmailChangeNewEmailCode(principal.accountId))
            }
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

@Serializable data class EmailVerificationCodeRequest(val email: String)
@Serializable data class CredentialsRequest(val email: String, val password: String)
@Serializable data class UpdateMeRequest(val email: String? = null, val password: String? = null)
@Serializable data class DeleteMeRequest(val password: String? = null)
@Serializable data class EmailVerificationRequest(val email: String, val code: String)
@Serializable data class EmailChangeCurrentVerificationRequest(val code: String)
@Serializable data class PasswordResetCodeRequest(val email: String)
@Serializable data class PasswordResetRequest(val email: String, val password: String, val code: String)

@Serializable
data class SignInRemoteResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("user_id") val userId: String,
)

private fun SignInPayload.toRemote() = SignInRemoteResponse(accessToken, tokenType, userId)
