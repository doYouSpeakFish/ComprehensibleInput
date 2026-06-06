package input.comprehensible.features.account

import input.comprehensible.enqueueSignInResult
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

/**
 * Steps covering the local user record the account module maintains for the signed-in user.
 */
class AccountUserStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope

    @Given("the sign in request will succeed with user id {string}")
    fun signInWillSucceedWithUserId(userId: String) {
        scope.fakeAccountRemoteDataSource.nextSignInUserId = userId
        scope.enqueueSignInResult(Result.success("token123"))
    }

    @Given("I am signed in as {string} with id {string}")
    fun iAmSignedInAsWithId(email: String, userId: String) {
        scope.signInAsBlocking(email, userId)
    }

    @Given("saving the local user record will fail")
    fun savingTheLocalUserRecordWillFail() {
        scope.userLocalDataSource.upsertError = RuntimeException("Unable to save local user record")
    }

    @Then("a local user record exists with id {string}")
    fun aLocalUserRecordExistsWithId(id: String) {
        assertTrue(scope.userRecordExists(id))
    }

    @Then("no local user record exists with id {string}")
    fun noLocalUserRecordExistsWithId(id: String) {
        assertFalse(scope.userRecordExists(id))
    }

    @Then("the current user id is {string}")
    fun theCurrentUserIdIs(id: String) {
        assertEquals(id, scope.currentUser()?.id)
    }

    @Then("there is no current user")
    fun thereIsNoCurrentUser() {
        assertNull(scope.currentUser())
    }
}
