package input.comprehensible.features.textadventure

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import input.comprehensible.ComprehensibleInputTestScope

class TextAdventureRobot(
    private val composeTestRule: ComposeContentTestRule,
) {
    fun assertMessageVisible(text: String) {
        composeTestRule
            .onNodeWithText(text, substring = true)
            .assertIsDisplayed()
    }

    fun tapOnSentence(sentence: String) {
        composeTestRule
            .onNodeWithText(sentence, substring = true)
            .performClick()
    }

    fun assertTranslationVisible(text: String) {
        composeTestRule
            .onNodeWithText(text, substring = true)
            .assertIsDisplayed()
    }

    fun enterResponse(text: String) {
        composeTestRule
            .onNodeWithTag("text_adventure_input")
            .performTextInput(text)
    }

    fun sendResponse() {
        composeTestRule
            .onNodeWithTag("text_adventure_send")
            .performClick()
    }

    fun assertInputIsHidden() {
        composeTestRule
            .onAllNodesWithTag("text_adventure_input")
            .assertCountEquals(0)
    }

    fun assertInputIsVisible() {
        composeTestRule
            .onNodeWithTag("text_adventure_input")
            .assertIsDisplayed()
    }
}

suspend fun ComprehensibleInputTestScope.onTextAdventure(
    block: suspend TextAdventureRobot.() -> Unit = {},
) = TextAdventureRobot(composeRule).apply { block() }
