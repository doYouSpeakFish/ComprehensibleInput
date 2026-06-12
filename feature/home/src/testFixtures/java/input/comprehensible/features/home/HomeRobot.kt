package input.comprehensible.features.home

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

class HomeRobot(private val composeTestRule: ComposeTestRule) {

    fun assertHomeScreenIsShown() {
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }

    fun assertStoriesOptionIsShown() {
        composeTestRule.onNodeWithTag("home_stories_card").assertIsDisplayed()
    }

    fun assertTextAdventuresOptionIsShown() {
        composeTestRule.onNodeWithTag("home_text_adventures_card").assertIsDisplayed()
    }

    fun assertTextAdventuresOptionIsHidden() {
        composeTestRule.onAllNodesWithTag("home_text_adventures_card").assertCountEquals(0)
    }

    fun assertSettingsActionIsShown() {
        composeTestRule.onNodeWithTag("home_settings_button").assertIsDisplayed()
    }

    fun selectStories() {
        composeTestRule.onNodeWithTag("home_stories_card").performClick()
    }

    fun selectTextAdventures() {
        composeTestRule.onNodeWithTag("home_text_adventures_card").performClick()
    }

    fun selectSettings() {
        composeTestRule.onNodeWithTag("home_settings_button").performClick()
    }
}
