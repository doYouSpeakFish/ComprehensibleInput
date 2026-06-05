package input.comprehensible.features.account

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the account sign in step, delegating Compose interaction to [AccountRobot]. The generic
 * error dialog is handled by [AccountCommonStepDefinitions].
 */
class SignInStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val account get() = AccountRobot(scope.composeRule)

    @Given("the account screen is open")
    fun theAccountScreenIsOpen() {
        scope.goToAccount()
        scope.idle()
    }

    @When("I enter the sign in email {string}")
    fun iEnterTheSignInEmail(email: String) {
        account.enterSignInEmail(email)
    }

    @When("I enter the sign in password {string}")
    fun iEnterTheSignInPassword(password: String) {
        account.enterSignInPassword(password)
    }

    @When("I submit the sign in form")
    fun iSubmitTheSignInForm() {
        account.tapSignIn()
        scope.idle()
    }

    @When("I tap the sign up button")
    fun iTapTheSignUpButton() {
        account.tapSignUpFromSignIn()
        scope.idle()
    }

    @When("I tap the forgot password button")
    fun iTapTheForgotPasswordButton() {
        account.tapForgotPassword()
        scope.idle()
    }

    @Then("the account title is shown")
    fun theAccountTitleIsShown() {
        account.assertAccountTitleIsVisible()
    }

    @Then("the sign in submit button is enabled")
    fun theSignInSubmitButtonIsEnabled() {
        account.assertSignInSubmitEnabled(isEnabled = true)
    }

    @Then("the sign in submit button is disabled")
    fun theSignInSubmitButtonIsDisabled() {
        account.assertSignInSubmitEnabled(isEnabled = false)
    }

    @Then("the sign in loading indicator is shown")
    fun theSignInLoadingIndicatorIsShown() {
        account.assertSignInLoadingIndicatorIsShown()
    }

    @Then("the sign up button is disabled")
    fun theSignUpButtonIsDisabled() {
        account.assertSignUpFromSignInEnabled(isEnabled = false)
    }

    @Then("the account sign in screen is shown")
    fun theAccountSignInScreenIsShown() {
        account.assertSignInScreenIsShown()
    }

    @Then("the sign in invalid credentials dialog is shown")
    fun theSignInInvalidCredentialsDialogIsShown() {
        account.invalidCredentialsDialog.assertIsShown()
    }

    @When("I dismiss the sign in invalid credentials dialog")
    fun iDismissTheSignInInvalidCredentialsDialog() {
        account.invalidCredentialsDialog.dismiss()
        scope.idle()
    }
}
