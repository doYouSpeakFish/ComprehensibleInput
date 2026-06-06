package input.comprehensible.features.home

import input.comprehensible.features.AppScenarioHolder
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Drives the home screen, delegating Compose interaction to [HomeRobot].
 */
class HomeStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val home get() = HomeRobot(scope.composeRule)

    @Given("the home screen is open")
    fun theHomeScreenIsOpen() {
        scope.goToHome()
        scope.idle()
    }

    @Then("the stories option is shown")
    fun theStoriesOptionIsShown() {
        home.assertStoriesOptionIsShown()
    }

    @Then("the text adventures option is shown")
    fun theTextAdventuresOptionIsShown() {
        home.assertTextAdventuresOptionIsShown()
    }

    @Then("the text adventures option is hidden")
    fun theTextAdventuresOptionIsHidden() {
        home.assertTextAdventuresOptionIsHidden()
    }

    @Then("the settings action is shown")
    fun theSettingsActionIsShown() {
        home.assertSettingsActionIsShown()
    }

    @When("I select the stories option")
    fun iSelectTheStoriesOption() {
        home.selectStories()
        scope.idle()
    }

    @When("I select the settings action")
    fun iSelectTheSettingsAction() {
        home.selectSettings()
        scope.idle()
    }

    @When("I select the text adventures option")
    fun iSelectTheTextAdventuresOption() {
        home.selectTextAdventures()
        scope.idle()
    }
}
