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
    fun selectStory(story: TestStory, learningLanguage: String = "de") {
        composeTestRule
            .onNodeWithText(
                when (learningLanguage) {
                    "de" -> story.germanTitle
                    "en" -> story.englishTitle
                    "es" -> story.spanishTitle
                    else -> error("Unknown language code: $learningLanguage")
                }
            )
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

    suspend fun setLearningLanguage(languageCode: String) {
        val languageContentDescription = when (languageCode) {
            "de" -> "Select German"
            "en" -> "Select English"
            "es" -> "Select Spanish"
            else -> error("Unknown language code: $languageCode")
        }
        composeTestRule.apply {
            onNodeWithContentDescription(
                label = "Select a language to learn",
                substring = true,
            ).performClick()
            awaitIdle()
            onNodeWithContentDescription(languageContentDescription).performClick()
        }
    }

    suspend fun setTranslationLanguage(languageCode: String) {
        val languageContentDescription = when (languageCode) {
            "de" -> "Select German"
            "en" -> "Select English"
            "es" -> "Select Spanish"
            else -> error("Unknown language code: $languageCode")
        }
        composeTestRule.apply {
            onNodeWithContentDescription(
                label = "Select a language for translations",
                substring = true,
            ).performClick()
            awaitIdle()
            onNodeWithContentDescription(languageContentDescription).performClick()
        }
    }

    fun assertStoryTitleIsVisible(title: String) {
        composeTestRule
            .onNodeWithText(title)
            .assertExists()
    }

    fun importStory() {
        TODO("Not yet implemented")
    }
}

suspend fun ComprehensibleInputTestScope.onStoryList(
    block: suspend StoryListRobot.() -> Unit = {}
) = StoryListRobot(composeRule).apply { block() }
