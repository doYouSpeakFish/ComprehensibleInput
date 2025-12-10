@file:OptIn(ExperimentalTestApi::class)

package input.comprehensible.features.story

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import input.comprehensible.ComprehensibleInputTestScope
import input.comprehensible.data.sample.TestStoryPart

class StoryReaderRobot(private val composeTestRule: ComposeContentTestRule) {

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

    fun assertStoryTextExists(sentence: String) {
        composeTestRule
            .onAllNodesWithText(sentence, substring = true, useUnmergedTree = true)
            .assertCountEquals(1)
    }

    fun assertChoiceIsShown(text: String) {
        composeTestRule
            .onNodeWithText(text, substring = true, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    fun waitForChoiceText(text: String) {
        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithText(text, substring = true, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun assertChoiceIsNotShown(text: String) {
        composeTestRule
            .onAllNodesWithText(text, useUnmergedTree = true)
            .assertCountEquals(0)
    }

    fun assertImageIsShown(image: TestStoryPart.Image) {
        composeTestRule
            .onNodeWithContentDescription(image.contentDescription)
            .assertIsDisplayed()
    }

    fun assertLoadingIndicatorIsShown() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("story_reader_loading"))
        composeTestRule
            .onNodeWithTag("story_reader_loading")
            .assertIsDisplayed()
    }

    fun assertErrorDialogIsShown() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("story_reader_error_dialog"))
        composeTestRule
            .onNodeWithTag("story_reader_error_dialog")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Unable to load story")
            .assertIsDisplayed()
    }

    fun dismissErrorDialog() {
        composeTestRule
            .onNodeWithText("OK")
            .performClick()
    }

    fun tapOnSentence(sentence: String) {
        composeTestRule
            .onNodeWithText(text = sentence, substring = true)
            .performTouchInput {
                click(Offset(1f, 1f))
            }
    }

    fun chooseStoryOption(text: String) {
        composeTestRule
            .onNode(
                hasContentDescription(value = text, substring = true),
                useUnmergedTree = true,
            )
            .performClick()
    }

    fun tapOnChoiceText(text: String) {
        composeTestRule
            .onNodeWithText(text, substring = true, useUnmergedTree = true)
            .performClick()
    }

    fun tapOnChosenChoiceText(text: String) {
        tapOnChoiceText(text)
    }

    fun swipeToNextPart() {
        composeTestRule
            .onNodeWithTag("story_reader_pager")
            .performTouchInput { swipeLeft() }
    }

    fun swipeToPreviousPart() {
        composeTestRule
            .onNodeWithTag("story_reader_pager")
            .performTouchInput { swipeRight() }
    }

    suspend fun skipToSentence(sentence: String) {
        val scrollNode = composeTestRule.onNodeWithTag(
            testTag = "story_reader_page_list",
            useUnmergedTree = true,
        )
        var index = 0
        while (!isSentenceDisplayed(sentence)) {
            try {
                scrollNode.performScrollToIndex(index)
            } catch (_: IllegalArgumentException) {
                break
            }
            composeTestRule.awaitIdle()
            index++
        }
        check(isSentenceDisplayed(sentence)) {
            val nodeCount = composeTestRule
                .onAllNodesWithText(sentence, substring = true, useUnmergedTree = true)
                .fetchSemanticsNodes().size
            "Sentence '$sentence' not found in story content (foundNodes=$nodeCount)"
        }
    }

    private fun isSentenceDisplayed(sentence: String) = composeTestRule
        .onNodeWithText(sentence, substring = true, useUnmergedTree = true)
        .isDisplayed()
}

suspend fun ComprehensibleInputTestScope.onStoryReader(
    block: suspend StoryReaderRobot.() -> Unit = {}
) = StoryReaderRobot(composeRule).apply { block() }
