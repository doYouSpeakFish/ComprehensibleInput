package input.comprehensible.features.account

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the password reset screen, delegating Compose interaction to [PasswordResetRobot]. The
 * generic error dialog is handled by [AccountCommonStepDefinitions].
 */
class PasswordResetStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val passwordReset get() = PasswordResetRobot(scope.composeRule)

    @Given("the password reset screen for {string} is open")
    fun thePasswordResetScreenIsOpen(email: String) {
        scope.goToPasswordReset(email)
        scope.idle()
    }

    @When("I enter the reset code {string}")
    fun iEnterTheResetCode(code: String) {
        passwordReset.enterResetCode(code)
    }

    @When("I enter the new password {string}")
    fun iEnterTheNewPassword(password: String) {
        passwordReset.enterNewPassword(password)
    }

    @When("I enter the new confirmation password {string}")
    fun iEnterTheNewConfirmationPassword(password: String) {
        passwordReset.enterConfirmNewPassword(password)
    }

    @When("I submit the password reset form")
    fun iSubmitThePasswordResetForm() {
        passwordReset.tapSubmit()
        scope.idle()
    }

    @When("I request a new reset code")
    fun iRequestANewResetCode() {
        passwordReset.tapResendCode()
        scope.idle()
    }

    @Then("the password reset submit button is enabled")
    fun thePasswordResetSubmitButtonIsEnabled() {
        passwordReset.assertSubmitIsEnabled()
    }

    @Then("the password reset submit button is disabled")
    fun thePasswordResetSubmitButtonIsDisabled() {
        passwordReset.assertSubmitIsDisabled()
    }

    @Then("the password reset loading indicator is shown")
    fun thePasswordResetLoadingIndicatorIsShown() {
        passwordReset.assertLoadingIndicatorIsShown()
    }

    @Then("the resend reset code button is enabled")
    fun theResendResetCodeButtonIsEnabled() {
        passwordReset.assertResendCodeIsEnabled()
    }

    @Then("the resend reset code button is disabled")
    fun theResendResetCodeButtonIsDisabled() {
        passwordReset.assertResendCodeIsDisabled()
    }

    @Then("the resend reset code loading indicator is shown")
    fun theResendResetCodeLoadingIndicatorIsShown() {
        passwordReset.assertResendLoadingIndicatorIsShown()
    }

    @Then("the reset code resent confirmation is shown")
    fun theResetCodeResentConfirmationIsShown() {
        passwordReset.assertCodeResentConfirmationIsShown()
    }

    @Then("the password reset screen shows the reset code message for {string}")
    fun thePasswordResetScreenShowsTheResetCodeMessage(email: String) {
        passwordReset.assertResetCodeMessageIsShown(email)
    }

    @Then("the invalid reset code dialog is shown")
    fun theInvalidResetCodeDialogIsShown() {
        passwordReset.invalidCodeErrorDialog.assertIsShown()
    }

    @When("I dismiss the invalid reset code dialog")
    fun iDismissTheInvalidResetCodeDialog() {
        passwordReset.invalidCodeErrorDialog.dismiss()
        scope.idle()
    }
}
