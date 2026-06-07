package input.comprehensible.features.account

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the email verification screen, delegating Compose interaction to [VerifyEmailRobot].
 */
class VerifyEmailStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val verifyEmail get() = VerifyEmailRobot(scope.composeRule)

    @Given("the email verification screen for {string} is open")
    fun theEmailVerificationScreenIsOpen(email: String) {
        scope.goToVerifyEmail(email)
        scope.idle()
    }

    @When("I enter the verification code {string}")
    fun iEnterTheVerificationCode(code: String) {
        verifyEmail.enterVerificationCode(code)
    }

    @When("I submit the verify email form")
    fun iSubmitTheVerifyEmailForm() {
        verifyEmail.tapVerifyEmailSubmit()
        scope.idle()
    }

    @When("I request a new verification code")
    fun iRequestANewVerificationCode() {
        verifyEmail.tapResendCode()
        scope.idle()
    }

    @Then("the verify email submit button is enabled")
    fun theVerifyEmailSubmitButtonIsEnabled() {
        verifyEmail.assertVerifyEmailSubmitIsEnabled()
    }

    @Then("the verify email submit button is disabled")
    fun theVerifyEmailSubmitButtonIsDisabled() {
        verifyEmail.assertVerifyEmailSubmitIsDisabled()
    }

    @Then("the verify email loading indicator is shown")
    fun theVerifyEmailLoadingIndicatorIsShown() {
        verifyEmail.assertVerifyEmailLoadingIndicatorIsShown()
    }

    @Then("the resend verification code button is enabled")
    fun theResendVerificationCodeButtonIsEnabled() {
        verifyEmail.assertResendCodeIsEnabled()
    }

    @Then("the resend verification code button is disabled")
    fun theResendVerificationCodeButtonIsDisabled() {
        verifyEmail.assertResendCodeIsDisabled()
    }

    @Then("the resend verification code loading indicator is shown")
    fun theResendVerificationCodeLoadingIndicatorIsShown() {
        verifyEmail.assertResendLoadingIndicatorIsShown()
    }

    @Then("the verification code resent confirmation is shown")
    fun theVerificationCodeResentConfirmationIsShown() {
        verifyEmail.assertCodeResentConfirmationIsShown()
    }

    @Then("the email verification screen shows the email {string}")
    fun theEmailVerificationScreenShowsTheEmail(email: String) {
        verifyEmail.assertEmailSentMessageIsShown(email)
    }
}
