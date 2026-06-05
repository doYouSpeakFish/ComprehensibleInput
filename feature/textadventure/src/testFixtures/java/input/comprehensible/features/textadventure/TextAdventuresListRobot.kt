package input.comprehensible.features.textadventure

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

/**
 * Drives and inspects the text adventures list screen in tests via stable test tags.
 */
class TextAdventuresListRobot(private val composeTestRule: ComposeTestRule) {

    fun assertScreenIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_screen").assertIsDisplayed()
    }

    fun assertSignInPromptIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_sign_in_prompt").assertIsDisplayed()
    }

    fun assertListIsHidden() {
        composeTestRule.onAllNodesWithTag("text_adventures_list").assertCountEquals(0)
    }

    fun tapSignIn() {
        composeTestRule.onNodeWithTag("text_adventures_sign_in_button").performClick()
    }

    fun assertEmptyMessageIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_empty").assertIsDisplayed()
    }

    fun assertLoadingIndicatorIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_loading").assertIsDisplayed()
    }

    fun assertErrorMessageIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_error").assertIsDisplayed()
    }

    fun assertAdventureIsListed(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title").assertIsDisplayed()
    }

    fun assertAdventureIsNotListed(title: String) {
        composeTestRule.onAllNodesWithTag("adventure_$title").assertCountEquals(0)
    }

    fun deleteAdventure(title: String) {
        composeTestRule.onNodeWithTag("delete_adventure_$title").performClick()
    }

    fun openAdventure(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title").performClick()
    }

    fun startNewAdventure() {
        composeTestRule.onNodeWithTag("text_adventures_new_button").performClick()
    }

    fun assertAccountScreenIsShown() {
        composeTestRule.onNodeWithTag("account_screen").assertIsDisplayed()
    }

    fun assertChatIsShown() {
        composeTestRule.onNodeWithTag("text_adventure_chat").assertIsDisplayed()
    }
}
