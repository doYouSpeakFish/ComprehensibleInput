package input.comprehensible.features.account

import input.comprehensible.data.account.InvalidCredentialsException
import input.comprehensible.data.account.InvalidResetCodeException
import input.comprehensible.enqueueDeleteAccountResult
import input.comprehensible.enqueueRequestPasswordResetCodeResult
import input.comprehensible.enqueueResetPasswordResult
import io.cucumber.java.en.Given

/**
 * Scripts the outcome of the account-management requests (delete account, request reset code, reset
 * password) that the fake account backend will return for the next call.
 */
class AccountManagementRequestStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope

    @Given("the delete account request will succeed")
    fun deleteAccountWillSucceed() {
        scope.enqueueDeleteAccountResult(Result.success(Unit))
    }

    @Given("the delete account request will fail with invalid credentials")
    fun deleteAccountWillFailWithInvalidCredentials() {
        scope.enqueueDeleteAccountResult(Result.failure(InvalidCredentialsException()))
    }

    @Given("the delete account request will fail")
    fun deleteAccountWillFail() {
        scope.enqueueDeleteAccountResult(Result.failure(Exception(NETWORK_ERROR)))
    }

    @Given("the password reset code request will succeed")
    fun requestResetCodeWillSucceed() {
        scope.enqueueRequestPasswordResetCodeResult(Result.success(Unit))
    }

    @Given("the password reset code request will fail")
    fun requestResetCodeWillFail() {
        scope.enqueueRequestPasswordResetCodeResult(Result.failure(Exception(NETWORK_ERROR)))
    }

    @Given("the password reset request will succeed")
    fun resetPasswordWillSucceed() {
        scope.enqueueResetPasswordResult(Result.success(Unit))
    }

    @Given("the password reset request will fail")
    fun resetPasswordWillFail() {
        scope.enqueueResetPasswordResult(Result.failure(Exception(NETWORK_ERROR)))
    }

    @Given("the password reset request will fail with an invalid code")
    fun resetPasswordWillFailWithInvalidCode() {
        scope.enqueueResetPasswordResult(Result.failure(InvalidResetCodeException()))
    }

    private companion object {
        const val NETWORK_ERROR = "Network error"
    }
}
