package input.comprehensible.features.textadventure

import input.comprehensible.features.AppScenarioHolder
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then

/**
 * App-level steps for reaching the text adventures screen from home. The screen's own behaviour is
 * covered by the Cucumber suite in :feature:textadventure; here we only verify it is reachable.
 */
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
