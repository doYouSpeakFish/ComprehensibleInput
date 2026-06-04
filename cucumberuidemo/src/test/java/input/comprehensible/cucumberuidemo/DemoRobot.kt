package input.comprehensible.cucumberuidemo

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

/**
 * Page-object / robot that hides the Compose testing APIs (node selectors, gestures and
 * assertions) behind user-facing actions, mirroring the robot pattern used elsewhere in this
 * project. The step definitions speak only in terms of these actions.
 */
class DemoRobot(private val composeRule: ComposeContentTestRule) {

    fun displayDemoScreen() {
        composeRule.setContent { DemoScreen() }
    }

    fun tapButton() {
        composeRule.onNodeWithTag("button").performClick()
    }

    fun assertGreetingShows(text: String) {
        composeRule.onNodeWithTag("greeting").assertTextEquals(text)
    }
}
