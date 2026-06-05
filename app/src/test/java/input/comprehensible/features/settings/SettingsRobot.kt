package input.comprehensible.features.settings

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

class SettingsRobot(private val composeTestRule: ComposeTestRule) {
    fun assertSettingsTitleIsVisible() {
        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()
    }

    fun assertAccountOptionIsVisible() {
        composeTestRule
            .onNodeWithText("Account")
            .assertIsDisplayed()
    }

    fun assertAccountOptionIsNotVisible() {
        composeTestRule
            .onAllNodesWithText("Account")
            .assertCountEquals(0)
    }

    fun openAccount() {
        composeTestRule
            .onNodeWithText("Account")
            .performClick()
    }

    fun assertSoftwareLicencesOptionIsVisible() {
        composeTestRule
            .onNodeWithText("Software licences")
            .assertIsDisplayed()
    }

    fun openSoftwareLicences() {
        composeTestRule
            .onNodeWithText("Software licences")
            .performClick()
    }

    fun navigateBackToStories() {
        composeTestRule
            .onNodeWithContentDescription("Navigate up")
            .performClick()
    }
}
