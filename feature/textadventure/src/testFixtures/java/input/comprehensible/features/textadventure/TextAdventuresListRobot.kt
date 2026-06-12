package input.comprehensible.features.textadventure

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeLeft

/**
 * Drives and inspects the text adventures list screen in tests via stable test tags.
 */
@Suppress("TooManyFunctions")
class TextAdventuresListRobot(private val composeTestRule: ComposeTestRule) {

    fun assertScreenIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_screen").assertIsDisplayed()
    }

    fun assertTitleIsShown() {
        composeTestRule.onNodeWithTag("text_adventures_title").assertIsDisplayed()
    }

    fun assertUpButtonIsShown() {
        composeTestRule.onNodeWithContentDescription("Navigate up").assertIsDisplayed()
    }

    fun tapUpButton() {
        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()
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

    // Adventures are deleted by swiping the row away towards the start.
    fun swipeToDeleteAdventure(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title").performTouchInput { swipeLeft() }
    }

    // A slow, short swipe that stays below the delete distance threshold (and, being slow, below
    // the fling velocity threshold), so the row springs back and remains.
    fun swipeAdventureWithoutDeleting(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title").performTouchInput {
            swipe(
                start = Offset(right, centerY),
                end = Offset(right - 60f, centerY),
                durationMillis = 1_000,
            )
        }
    }

    // The undo action and the deleted message live in the snackbar, which has no stable tag of its
    // own, so they are reached through their user-visible text.
    fun undoAdventureDeletion() {
        composeTestRule.onNodeWithText("Undo").performClick()
    }

    fun assertAdventureDeletedMessageIsShown() {
        composeTestRule.onNodeWithText("Adventure deleted").assertIsDisplayed()
    }

    fun assertAdventureDeletedMessageIsNotShown() {
        composeTestRule.onAllNodesWithText("Adventure deleted").assertCountEquals(0)
    }

    fun openAdventure(title: String) {
        composeTestRule.onNodeWithTag("adventure_$title").performClick()
    }

    fun startNewAdventure() {
        composeTestRule.onNodeWithTag("text_adventures_new_button").performClick()
    }

    // The language picker is driven through its content descriptions (it is the same component as
    // the story list's, which carries no test tags): the toggle buttons describe the current
    // selection and each menu entry is described as "Select <language>".
    fun setLearningLanguage(languageName: String) {
        composeTestRule.apply {
            onNodeWithContentDescription("Select a language to learn", substring = true)
                .performClick()
            waitForIdle()
            onNodeWithContentDescription("Select $languageName").performClick()
        }
    }

    fun setTranslationLanguage(languageName: String) {
        composeTestRule.apply {
            onNodeWithContentDescription("Select a language for translations", substring = true)
                .performClick()
            waitForIdle()
            onNodeWithContentDescription("Select $languageName").performClick()
        }
    }

    fun assertLearningLanguageIs(languageName: String) {
        composeTestRule
            .onNodeWithContentDescription("Select a language to learn. Currently learning $languageName")
            .assertIsDisplayed()
    }

    fun assertTranslationLanguageIs(languageName: String) {
        composeTestRule
            .onNodeWithContentDescription(
                "Select a language for translations. Currently translating into $languageName",
            )
            .assertIsDisplayed()
    }

    fun assertAccountScreenIsShown() {
        composeTestRule.onNodeWithTag("account_screen").assertIsDisplayed()
    }

    fun assertChatIsShown() {
        composeTestRule.onNodeWithTag("text_adventure_chat").assertIsDisplayed()
    }
}
