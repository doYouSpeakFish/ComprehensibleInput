package input.comprehensible.features.story

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import input.comprehensible.ComprehensibleInputTestScope

class StoryReaderRobot(private val composeTestRule: ComposeTestRule) {

    fun assertStoryTitleIsShown(title: String) {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    fun assertStoryLineIsVisible(line: String) {
        composeTestRule.onNodeWithText(line).assertIsDisplayed()
    }
}

suspend fun ComprehensibleInputTestScope.onStoryReader(
    block: suspend StoryReaderRobot.() -> Unit = {}
) = StoryReaderRobot(composeRule).apply { block() }
