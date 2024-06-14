package input.comprehensible.features.story

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import input.comprehensible.ComprehensibleInputTestScope
import input.comprehensible.data.sample.TestStoryPart

class StoryReaderRobot(private val composeTestRule: ComposeTestRule) {

    fun assertStoryTitleIsShown(title: String) {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    fun assertStoryTextIsVisible(line: String) {
        composeTestRule.onNodeWithText(line).assertIsDisplayed()
    }

    fun assertImageIsShown(image: TestStoryPart.Image) {
        composeTestRule
            .onNodeWithContentDescription(image.contentDescription)
            .assertIsDisplayed()
    }
}

suspend fun ComprehensibleInputTestScope.onStoryReader(
    block: suspend StoryReaderRobot.() -> Unit = {}
) = StoryReaderRobot(composeRule).apply { block() }
