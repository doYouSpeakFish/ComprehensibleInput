package input.comprehensible.features.settings

import input.comprehensible.features.AppScenarioHolder
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the settings screen, delegating Compose interaction to [SettingsRobot].
 */
class SettingsStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val settings get() = SettingsRobot(scope.composeRule)

    @Given("the settings screen is open")
    fun theSettingsScreenIsOpen() {
        scope.goToSettings()
        scope.idle()
    }

    @Then("the settings title is shown")
    fun theSettingsTitleIsShown() {
        settings.assertSettingsTitleIsVisible()
    }

    @Then("the account option is shown")
    fun theAccountOptionIsShown() {
        settings.assertAccountOptionIsVisible()
    }

    @Then("the account option is not shown")
    fun theAccountOptionIsNotShown() {
        settings.assertAccountOptionIsNotVisible()
    }

    @When("the reader opens the account option")
    fun theReaderOpensTheAccountOption() {
        settings.openAccount()
        scope.idle()
    }

    @Then("the software licences option is shown")
    fun theSoftwareLicencesOptionIsShown() {
        settings.assertSoftwareLicencesOptionIsVisible()
    }

    @When("the reader opens the software licences option")
    fun theReaderOpensTheSoftwareLicencesOption() {
        settings.openSoftwareLicences()
        scope.idle()
    }

    @When("the reader navigates up from settings")
    fun theReaderNavigatesUpFromSettings() {
        settings.navigateBackToStories()
        scope.idle()
    }
}
