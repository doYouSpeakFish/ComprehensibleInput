package input.comprehensible.cucumberuidemo

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Ordinary Cucumber step definitions. They look exactly like any other Cucumber glue, but they
 * interact with a live Compose UI through the rule published by [ComposeRuleHolder].
 */
class DemoStepDefinitions {
    private val composeRule get() = ComposeRuleHolder.composeRule

    @Given("the demo screen is displayed")
    fun theDemoScreenIsDisplayed() {
        composeRule.setContent { DemoScreen() }
    }

    @When("I tap the button")
    fun iTapTheButton() {
        composeRule.onNodeWithTag("button").performClick()
    }

    @Then("the greeting shows {string}")
    fun theGreetingShows(expected: String) {
        composeRule.onNodeWithTag("greeting").assertTextEquals(expected)
    }
}
