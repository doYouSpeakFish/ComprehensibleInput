package input.comprehensible.features.softwarelicences

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import input.comprehensible.ComprehensibleInputTestScope

class SoftwareLicencesRobot(private val composeTestRule: ComposeTestRule) {
    fun assertLicenceIsVisible(softwareLicence: String) {
        composeTestRule
            .onNodeWithText(softwareLicence)
            .performScrollTo()
            .assertIsDisplayed()
    }
}

suspend fun ComprehensibleInputTestScope.onSoftwareLicences(
    block: suspend SoftwareLicencesRobot.() -> Unit = {}
) = SoftwareLicencesRobot(composeRule).apply { block() }
