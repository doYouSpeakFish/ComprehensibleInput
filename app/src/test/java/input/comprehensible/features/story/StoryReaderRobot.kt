package input.comprehensible.features.story

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import input.comprehensible.ComprehensibleInputTestScope
import input.comprehensible.data.sample.TestStoryPart

class StoryReaderRobot(private val composeTestRule: ComposeTestRule) {

    fun setLanguage(language: String) {
        val contentDescription = when (language) {
            "de" -> "Select German"
            "en" -> "Select English"
            else -> error("Unsupported language: $language")
        }
        composeTestRule
            .onNodeWithContentDescription(contentDescription)
            .performClick()
    }

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
