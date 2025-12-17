package input.comprehensible.features.softwarelicences

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performScrollTo
import input.comprehensible.ComprehensibleInputTestScope

class SoftwareLicencesRobot(private val composeTestRule: ComposeTestRule) {
    fun assertSoftwareLicencesTitleIsVisible() {
        composeTestRule
            .onAllNodesWithText("Software licences")
            .onFirst()
            .assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilLicencesHaveLoaded() {
        composeTestRule.waitUntilAtLeastOneExists(
            matcher = hasText("Apache License 2.0"),
            timeoutMillis = 2000L,
        )
    }

    @OptIn(ExperimentalTestApi::class)
    fun assertLicenceIsVisible(softwareLicence: String) {
        composeTestRule
            .onAllNodesWithText(softwareLicence)
            .onFirst()
            .performScrollTo()
            .assertIsDisplayed()
    }
}

suspend fun ComprehensibleInputTestScope.onSoftwareLicences(
    block: suspend SoftwareLicencesRobot.() -> Unit = {}
) = SoftwareLicencesRobot(composeRule).apply { block() }
