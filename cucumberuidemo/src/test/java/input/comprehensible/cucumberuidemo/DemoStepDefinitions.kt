package input.comprehensible.cucumberuidemo

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Ordinary Cucumber step definitions. They read as user-facing actions and delegate all Compose
 * interaction to [DemoRobot], which is driven by the rule the host runner publishes through
 * [ComposeRuleHolder].
 */
class DemoStepDefinitions {
    private val robot get() = DemoRobot(ComposeRuleHolder.composeRule)

    @Given("the demo screen is displayed")
    fun theDemoScreenIsDisplayed() {
        robot.displayDemoScreen()
    }

    @When("I tap the button")
    fun iTapTheButton() {
        robot.tapButton()
    }

    @Then("the greeting shows {string}")
    fun theGreetingShows(expected: String) {
        robot.assertGreetingShows(expected)
    }
}
