package input.comprehensible.features.account

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the sign up screen. Compose interaction is delegated to [SignUpRobot]; the generic error
 * dialog is handled by [AccountCommonStepDefinitions].
 */
class SignUpStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val signUp get() = SignUpRobot(scope.composeRule)

    @Given("the sign up screen is open")
    fun theSignUpScreenIsOpen() {
        scope.goToSignUp()
        scope.idle()
    }

    @Given("the sign up form is completed for {string}")
    fun theSignUpFormIsCompletedFor(email: String) {
        signUp.enterEmail(email)
        signUp.enterPassword(VALID_PASSWORD)
        signUp.enterConfirmPassword(VALID_PASSWORD)
    }

    @When("I enter the sign up email {string}")
    fun iEnterTheSignUpEmail(email: String) {
        signUp.enterEmail(email)
    }

    @When("I enter the sign up password {string}")
    fun iEnterTheSignUpPassword(password: String) {
        signUp.enterPassword(password)
    }

    @When("I enter the sign up confirmation password {string}")
    fun iEnterTheSignUpConfirmationPassword(password: String) {
        signUp.enterConfirmPassword(password)
    }

    @When("I submit the sign up form")
    fun iSubmitTheSignUpForm() {
        signUp.tapSignUpSubmit()
        scope.idle()
    }

    @Then("the sign up submit button is enabled")
    fun theSignUpSubmitButtonIsEnabled() {
        signUp.assertSignUpSubmitIsEnabled()
    }

    @Then("the sign up submit button is disabled")
    fun theSignUpSubmitButtonIsDisabled() {
        signUp.assertSignUpSubmitIsDisabled()
    }

    @Then("the sign up loading indicator is shown")
    fun theSignUpLoadingIndicatorIsShown() {
        signUp.assertSignUpLoadingIndicatorIsShown()
    }

    private companion object {
        const val VALID_PASSWORD = "password12345"
    }
}
