@file:OptIn(ExperimentalTestApi::class)

package input.comprehensible.features.textadventure

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import input.comprehensible.ComprehensibleInputTestScope

class TextAdventureRobot(private val composeTestRule: ComposeContentTestRule) {
    fun assertScenarioIsShown(text: String) {
        composeTestRule.onNodeWithText(text, substring = true).assertIsDisplayed()
    }

    fun assertAiResponseIsShown(text: String) {
        composeTestRule.onNodeWithText(text, substring = true).assertIsDisplayed()
    }

    fun assertUserResponseIsShown(text: String) {
        composeTestRule.onNodeWithText(text, substring = true).assertIsDisplayed()
    }

    fun enterResponse(text: String) {
        composeTestRule.onNodeWithTag("text_adventure_input").performTextInput(text)
    }

    fun sendResponse() {
        composeTestRule.onNodeWithTag("text_adventure_send").performClick()
    }

    fun assertInputIsHidden() {
        composeTestRule.onAllNodesWithTag("text_adventure_input").assertCountEquals(0)
    }

    fun tapOnSentence(sentence: String) {
        composeTestRule
            .onNodeWithText(text = sentence, substring = true)
            .performTouchInput { click(Offset(1f, 1f)) }
    }

    fun assertTranslationIsShown(translation: String) {
        composeTestRule.onNodeWithText(translation, substring = true).assertIsDisplayed()
    }

    suspend fun awaitIdle() {
        composeTestRule.awaitIdle()
    }
}

suspend fun ComprehensibleInputTestScope.onTextAdventure(
    block: suspend TextAdventureRobot.() -> Unit = {}
) = TextAdventureRobot(composeRule).apply { block() }
