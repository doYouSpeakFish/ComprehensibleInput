@file:OptIn(ExperimentalTestApi::class)

package input.comprehensible.features.story

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
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

    fun assertStoryTextIsVisible(sentence: String) {
        composeTestRule
            .onNodeWithText(text = sentence, substring = true)
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

    suspend fun skipToSentence(sentence: String) {
        var i = 0
        while (!isSentenceDisplayed(sentence)) {
            composeTestRule
                .onNode(hasScrollAction())
                .performScrollToIndex(i)
            composeTestRule.awaitIdle()
            i++
        }
    }

    private fun isSentenceDisplayed(sentence: String) = composeTestRule
        .onNodeWithText(sentence, substring = true, useUnmergedTree = true)
        .isDisplayed()
}

suspend fun ComprehensibleInputTestScope.onStoryReader(
    block: suspend StoryReaderRobot.() -> Unit = {}
) = StoryReaderRobot(composeRule).apply { block() }
