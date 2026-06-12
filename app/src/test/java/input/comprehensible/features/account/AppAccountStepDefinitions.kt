package input.comprehensible.features.account

import input.comprehensible.features.AppScenarioHolder
import io.cucumber.java.en.Then

/**
 * Assertions about the account screen as reached from within the app (via settings). The account
 * feature's own behaviour is covered by the Cucumber suite in :feature-account.
 */
class AppAccountStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val account get() = AccountRobot(scope.composeRule)

    @Then("the account screen title is shown")
    fun theAccountScreenTitleIsShown() {
        account.assertAccountTitleIsVisible()
    }
}
