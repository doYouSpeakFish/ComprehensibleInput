package input.comprehensible.features.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import input.comprehensible.ComprehensibleInputTestScope

class SettingsRobot(private val composeTestRule: ComposeTestRule) {
    fun assertSettingsTitleIsVisible() {
        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()
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

suspend fun ComprehensibleInputTestScope.onSettings(
    block: suspend SettingsRobot.() -> Unit = {}
) = SettingsRobot(composeRule).apply { block() }
