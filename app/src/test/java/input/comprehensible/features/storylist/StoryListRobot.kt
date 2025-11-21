package input.comprehensible.features.storylist

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import input.comprehensible.ComprehensibleInputTestScope
import input.comprehensible.data.sample.TestStory

class StoryListRobot(
    private val composeTestRule: ComposeTestRule
) {
    private fun languageNameFor(languageCode: String) = when (languageCode) {
        "de" -> "German"
        "en" -> "English"
        "es" -> "Spanish"
        "fr" -> "French"
        "pt" -> "Portuguese"
        "id" -> "Indonesian"
        else -> error("Unknown language code: $languageCode")
    }

    private fun learningLanguageContentDescription(languageCode: String) =
        "Select a language to learn. Currently learning ${languageNameFor(languageCode)}"

    private fun translationLanguageContentDescription(languageCode: String) =
        "Select a language for translations. Currently translating into ${languageNameFor(languageCode)}"

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

    fun assertStoryImageIsVisible(storyId: String) {
        composeTestRule
            .onNodeWithTag(
                testTag = "story-image-$storyId",
                useUnmergedTree = true,
            )
            .assertExists()
    }

    fun openSettings() {
        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()
    }

    fun assertLearningLanguageIs(languageCode: String) {
        composeTestRule
            .onNodeWithContentDescription(learningLanguageContentDescription(languageCode))
            .assertExists()
    }

    fun assertTranslationLanguageIs(languageCode: String) {
        composeTestRule
            .onNodeWithContentDescription(translationLanguageContentDescription(languageCode))
            .assertExists()
    }

    fun assertStoryIsNotVisible(story: TestStory, learningLanguage: String = "de") {
        composeTestRule
            .onAllNodesWithText(
                when (learningLanguage) {
                    "de" -> story.germanTitle
                    "en" -> story.englishTitle
                    "es" -> story.spanishTitle
                    else -> error("Unknown language code: $learningLanguage")
                }
            )
            .assertCountEquals(0)
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
}

suspend fun ComprehensibleInputTestScope.onStoryList(
    block: suspend StoryListRobot.() -> Unit = {}
) = StoryListRobot(composeRule).apply { block() }
