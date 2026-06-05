package input.comprehensible.features.account

import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the signed-in account screen (signed-in email, sign out, the delete account entry point),
 * delegating Compose interaction to [AccountRobot].
 */
class SignedInStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val account get() = AccountRobot(scope.composeRule)

    @Then("the signed in email {string} is shown")
    fun theSignedInEmailIsShown(email: String) {
        account.assertSignedInEmailIsShown(email)
    }

    @When("I tap the sign out button")
    fun iTapTheSignOutButton() {
        account.tapSignOut()
        scope.idle()
    }

    @Then("the delete account button is shown")
    fun theDeleteAccountButtonIsShown() {
        account.assertDeleteAccountButtonIsShown()
    }

    @When("I tap the delete account button")
    fun iTapTheDeleteAccountButton() {
        account.tapDeleteAccount()
        scope.idle()
    }
}
