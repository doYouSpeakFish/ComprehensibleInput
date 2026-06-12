package input.comprehensible.features.account

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the delete account screen, delegating Compose interaction to [DeleteAccountRobot]. The
 * generic error dialog is handled by [AccountCommonStepDefinitions].
 */
class DeleteAccountStepDefinitions {
    private val scope get() = AccountScenarioHolder.scope
    private val deleteAccount get() = DeleteAccountRobot(scope.composeRule)

    @Given("the delete account screen is open")
    fun theDeleteAccountScreenIsOpen() {
        scope.goToDeleteAccount()
        scope.idle()
    }

    @When("I enter the delete account password {string}")
    fun iEnterTheDeleteAccountPassword(password: String) {
        deleteAccount.enterPassword(password)
    }

    @When("I reveal the delete account password")
    fun iRevealTheDeleteAccountPassword() {
        deleteAccount.passwordField.toggleVisibility()
        scope.idle()
    }

    @When("I submit the delete account form")
    fun iSubmitTheDeleteAccountForm() {
        deleteAccount.tapSubmit()
        scope.idle()
    }

    @Then("the delete account submit button is enabled")
    fun theDeleteAccountSubmitButtonIsEnabled() {
        deleteAccount.assertSubmitIsEnabled()
    }

    @Then("the delete account submit button is disabled")
    fun theDeleteAccountSubmitButtonIsDisabled() {
        deleteAccount.assertSubmitIsDisabled()
    }

    @Then("the delete account loading indicator is shown")
    fun theDeleteAccountLoadingIndicatorIsShown() {
        deleteAccount.assertLoadingIndicatorIsShown()
    }

    @Then("the delete account password is shown")
    fun theDeleteAccountPasswordIsShown() {
        deleteAccount.passwordField.assertVisible()
    }

    @Then("the delete account password is hidden")
    fun theDeleteAccountPasswordIsHidden() {
        deleteAccount.passwordField.assertHidden()
    }

    @Then("the delete account explainer is shown")
    fun theDeleteAccountExplainerIsShown() {
        deleteAccount.assertExplainerIsShown()
    }

    @Then("the delete account warning is shown")
    fun theDeleteAccountWarningIsShown() {
        deleteAccount.assertWarningIsShown()
    }

    @Then("the delete account invalid credentials dialog is shown")
    fun theDeleteAccountInvalidCredentialsDialogIsShown() {
        deleteAccount.invalidCredentialsDialog.assertIsShown()
    }

    @When("I dismiss the delete account invalid credentials dialog")
    fun iDismissTheDeleteAccountInvalidCredentialsDialog() {
        deleteAccount.invalidCredentialsDialog.dismiss()
        scope.idle()
    }
}
