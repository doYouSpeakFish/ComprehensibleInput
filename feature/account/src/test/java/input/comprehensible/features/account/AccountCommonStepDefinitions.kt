package input.comprehensible.features.account

import input.comprehensible.delayAccountRequests
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Step definitions shared across the account screens: scripting requests to stay in flight, the
 * signed-in precondition, and the generic error dialog (which uses one common tag everywhere).
 */
class AccountCommonStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val errorDialog get() = GenericErrorDialogRobot(scope.composeRule)

    @Given("account requests are delayed")
    fun accountRequestsAreDelayed() {
        scope.delayAccountRequests(IN_FLIGHT_DELAY_MILLIS)
    }

    @When("{int} second(s) pass(es)")
    fun secondsPass(seconds: Int) {
        scope.advanceTimeBy(seconds * MILLIS_PER_SECOND)
    }

    @When("the resend code cooldown elapses")
    fun theResendCodeCooldownElapses() {
        scope.advanceUntilIdle()
    }

    @Given("I am signed in as {string}")
    fun iAmSignedInAs(email: String) {
        scope.signInAsBlocking(email)
    }

    @Then("the error dialog is shown")
    fun theErrorDialogIsShown() {
        errorDialog.assertIsShown()
    }

    @When("I dismiss the error dialog")
    fun iDismissTheErrorDialog() {
        errorDialog.dismiss()
        scope.idle()
    }

    private companion object {
        const val IN_FLIGHT_DELAY_MILLIS = 1_000L
        const val MILLIS_PER_SECOND = 1_000L
    }
}
