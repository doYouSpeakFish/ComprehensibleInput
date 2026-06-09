package input.comprehensible.features.textadventure

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft

/**
 * Drives and inspects the text adventures list screen in tests via stable test tags.
 */
@Suppress("TooManyFunctions")
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

    fun assertEarlyAccessNoticeIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_early_access").assertIsDisplayed()
    }

    fun assertBusyMessageIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_busy").assertIsDisplayed()
    }

    fun assertAdventureIsListed(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title").assertIsDisplayed()
    }

    fun assertAdventureIsNotListed(title: String) {
        composeTestRule.onAllNodesWithTag("adventure_$title").assertCountEquals(0)
    }

    // The image sits inside the row Card's `clickable` merge boundary, so it is queried on the
    // unmerged tree to resolve it as a distinct node rather than being folded into the card.
    fun assertAdventureImageIsShown(title: String) {
        composeTestRule.onNodeWithTag("adventure_image_$title", useUnmergedTree = true).assertIsDisplayed()
    }

    fun assertAdventureImageIsNotShown(title: String) {
        composeTestRule.onAllNodesWithTag("adventure_image_$title", useUnmergedTree = true).assertCountEquals(0)
    }

    // Adventures are deleted by swiping the row towards the start, so the gesture drives the
    // swipe-to-dismiss container rather than tapping a button.
    fun deleteAdventure(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title").performTouchInput { swipeLeft() }
    }

    // A short swipe that stays below the delete threshold, so the row springs back and remains.
    fun swipeAdventureWithoutDeleting(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title")
            .performTouchInput { swipeLeft(startX = right, endX = right - 60f) }
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
