package input.comprehensible.features.account

import input.comprehensible.data.account.InvalidCredentialsException
import input.comprehensible.enqueueCreateAccountResult
import input.comprehensible.enqueueRequestEmailVerificationCodeResult
import input.comprehensible.enqueueSignInResult
import input.comprehensible.enqueueVerifyEmailResult
import io.cucumber.java.en.Given

/**
 * Scripts the outcome of the authentication requests (create account, verify email, sign in) that
 * the fake account backend will return for the next call.
 */
class AuthRequestStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope

    @Given("the create account request will succeed")
    fun createAccountWillSucceed() {
        scope.enqueueCreateAccountResult(Result.success(Unit))
    }

    @Given("the create account request will fail")
    fun createAccountWillFail() {
        scope.enqueueCreateAccountResult(Result.failure(Exception(NETWORK_ERROR)))
    }

    @Given("the verify email request will succeed")
    fun verifyEmailWillSucceed() {
        scope.enqueueVerifyEmailResult(Result.success(Unit))
    }

    @Given("the verify email request will fail")
    fun verifyEmailWillFail() {
        scope.enqueueVerifyEmailResult(Result.failure(Exception(NETWORK_ERROR)))
    }

    @Given("the email verification code request will succeed")
    fun requestEmailVerificationCodeWillSucceed() {
        scope.enqueueRequestEmailVerificationCodeResult(Result.success(Unit))
    }

    @Given("the email verification code request will fail")
    fun requestEmailVerificationCodeWillFail() {
        scope.enqueueRequestEmailVerificationCodeResult(Result.failure(Exception(NETWORK_ERROR)))
    }

    @Given("the sign in request will succeed")
    fun signInWillSucceed() {
        scope.enqueueSignInResult(Result.success("token123"))
    }

    @Given("the sign in request will fail with invalid credentials")
    fun signInWillFailWithInvalidCredentials() {
        scope.enqueueSignInResult(Result.failure(InvalidCredentialsException()))
    }

    @Given("the sign in request will fail")
    fun signInWillFail() {
        scope.enqueueSignInResult(Result.failure(Exception(NETWORK_ERROR)))
    }

    private companion object {
        const val NETWORK_ERROR = "Network error"
    }
}
