package input.comprehensible.features.account

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the forgot password screen, delegating Compose interaction to [ForgotPasswordRobot]. The
 * generic error dialog is handled by [AccountCommonStepDefinitions].
 */
class ForgotPasswordStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val forgotPassword get() = ForgotPasswordRobot(scope.composeRule)

    @Given("the forgot password screen is open")
    fun theForgotPasswordScreenIsOpen() {
        scope.goToForgotPassword()
        scope.idle()
    }

    @When("I enter the forgot password email {string}")
    fun iEnterTheForgotPasswordEmail(email: String) {
        forgotPassword.enterEmail(email)
    }

    @When("I submit the forgot password form")
    fun iSubmitTheForgotPasswordForm() {
        forgotPassword.tapSubmit()
        scope.idle()
    }

    @Then("the forgot password submit button is enabled")
    fun theForgotPasswordSubmitButtonIsEnabled() {
        forgotPassword.assertSubmitIsEnabled()
    }

    @Then("the forgot password submit button is disabled")
    fun theForgotPasswordSubmitButtonIsDisabled() {
        forgotPassword.assertSubmitIsDisabled()
    }

    @Then("the forgot password loading indicator is shown")
    fun theForgotPasswordLoadingIndicatorIsShown() {
        forgotPassword.assertLoadingIndicatorIsShown()
    }
}
