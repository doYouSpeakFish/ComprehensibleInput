package input.comprehensible.features.story

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import input.comprehensible.ComprehensibleInputTestScope
import input.comprehensible.data.sample.TestStoryPart

class StoryReaderRobot(private val composeTestRule: ComposeTestRule) {

    fun assertStoryTitleIsShown(title: String) {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    fun assertStoryTextIsVisible(sentences: List<String>) {
        composeTestRule
            .onNodeWithText(text = sentences.first(), substring = true)
            .assertIsDisplayed()
    }

    fun assertImageIsShown(image: TestStoryPart.Image) {
        composeTestRule
            .onNodeWithContentDescription(image.contentDescription)
            .assertIsDisplayed()
    }

    fun tapOnSentence(sentence: String) {
        composeTestRule
            .onNodeWithText(text = sentence, substring = true)
            .performTouchInput {
                click(Offset(1f, 1f))
            }
    }
}

suspend fun ComprehensibleInputTestScope.onStoryReader(
    block: suspend StoryReaderRobot.() -> Unit = {}
) = StoryReaderRobot(composeRule).apply { block() }
