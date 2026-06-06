package input.comprehensible.features.textadventure

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput

/**
 * Drives and inspects the text adventure chat screen in tests.
 */
@Suppress("TooManyFunctions")
class TextAdventureChatRobot(private val composeTestRule: ComposeTestRule) {

    fun assertChatIsShown() {
        composeTestRule.onNodeWithTag("text_adventure_chat").assertIsDisplayed()
    }

    fun assertPlaceholderIsShown() {
        composeTestRule.onNodeWithTag("generating_placeholder").assertIsDisplayed()
    }

    fun assertPlaceholderIsHidden() {
        composeTestRule.onAllNodesWithTag("generating_placeholder").assertCountEquals(0)
    }

    fun assertShowsText(text: String) {
        composeTestRule.onNodeWithText(text, substring = true).assertIsDisplayed()
    }

    fun assertErrorIsShown() {
        composeTestRule.onNodeWithTag("generation_error").assertIsDisplayed()
    }

    fun assertMessageErrorIsShown() {
        composeTestRule.onNodeWithTag("message_error").assertIsDisplayed()
    }

    fun assertBusyMessageIsShown() {
        composeTestRule.onNodeWithTag("busy_error").assertIsDisplayed()
    }

    fun assertInputIsHidden() {
        composeTestRule.onAllNodesWithTag("message_input").assertCountEquals(0)
    }

    fun assertInputIsDisabled() {
        composeTestRule.onNodeWithTag("message_input").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("send_message_button").assertIsNotEnabled()
    }

    fun assertErrorIsHidden() {
        composeTestRule.onAllNodesWithTag("generation_error").assertCountEquals(0)
    }

    fun assertRetryIsShown() {
        composeTestRule.onNodeWithTag("retry_button").assertIsDisplayed()
    }

    fun tapRetry() {
        composeTestRule.onNodeWithTag("retry_button").performClick()
    }

    fun tapSentence(text: String) {
        composeTestRule
            .onNodeWithText(text, substring = true)
            .performTouchInput { click(Offset(1f, 1f)) }
    }

    fun sendMessage(text: String) {
        composeTestRule.onNodeWithTag("message_input").performTextInput(text)
        composeTestRule.onNodeWithTag("send_message_button").performClick()
    }

    /** The text currently shown by the generating placeholder, for verifying it cycles. */
    fun placeholderText(): String {
        val node = composeTestRule.onNodeWithTag("generating_placeholder").fetchSemanticsNode()
        return node.config.getOrNull(SemanticsProperties.Text)?.joinToString("") { it.text }.orEmpty()
    }
}
