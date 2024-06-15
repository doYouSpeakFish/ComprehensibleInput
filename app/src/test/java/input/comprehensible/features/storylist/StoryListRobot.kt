package input.comprehensible.features.storylist

import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import input.comprehensible.ComprehensibleInputTestScope
import input.comprehensible.data.sample.TestStory

class StoryListRobot(
    private val composeTestRule: ComposeTestRule
) {
    fun selectStory(story: TestStory) {
        composeTestRule
            .onNodeWithText(story.title)
            .performScrollTo()
            .performClick()
    }

    fun findStory(index: Int) {
        composeTestRule
            .onNode(hasScrollAction())
            .performScrollToIndex(index)
            .performClick()
    }

    fun assertStoryImageIsVisible(contentDescription: String) {
        composeTestRule
            .onNodeWithContentDescription(contentDescription)
            .assertExists()
    }
}

suspend fun ComprehensibleInputTestScope.onStoryList(
    block: suspend StoryListRobot.() -> Unit = {}
) = StoryListRobot(composeRule).apply { block() }
