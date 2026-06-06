package input.comprehensible.features.textadventure

import input.comprehensible.features.AppScenarioHolder
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then

class TextAdventureStepDefinitions {
    private val scope get() = AppScenarioHolder.scope
    private val robot get() = TextAdventuresListRobot(scope.composeRule)

    @Given("I am signed in as {string}")
    fun iAmSignedInAs(email: String) {
        scope.signInAs(email)
    }

    @Then("the text adventures screen is shown")
    fun theTextAdventuresScreenIsShown() {
        robot.assertScreenIsShown()
    }
}
