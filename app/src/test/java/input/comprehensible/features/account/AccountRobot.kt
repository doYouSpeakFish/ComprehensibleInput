package input.comprehensible.features.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import input.comprehensible.ComprehensibleInputTestScope

class AccountRobot(private val composeTestRule: ComposeTestRule) {
    fun assertAccountTitleIsVisible() {
        composeTestRule
            .onNodeWithText("Account")
            .assertIsDisplayed()
    }
}

suspend fun ComprehensibleInputTestScope.onAccount(
    block: suspend AccountRobot.() -> Unit = {}
) = AccountRobot(composeRule).apply { block() }
