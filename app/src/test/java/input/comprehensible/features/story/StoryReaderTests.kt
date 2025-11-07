package input.comprehensible.features.story

import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.data.StoriesTestData
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.data.sample.TestStoryPart
import input.comprehensible.features.storylist.onStoryList
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import javax.inject.Inject

@RunWith(ParameterizedRobolectricTestRunner::class)
@HiltAndroidTest
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    application = HiltTestApplication::class,
    qualifiers = "w360dp-h640dp-mdpi",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@OptIn(ExperimentalRoborazziApi::class)
class StoryReaderTests(private val themeMode: ThemeMode) {

    @get:Rule
    val testRule = ComprehensibleInputTestRule(this, themeMode)

    @Inject
    lateinit var storiesData: StoriesTestData

    @Test
    fun `story reader screenshot`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)

        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            assertStoryTitleIsShown(stories.first().germanTitle)
            assertStoryTextIsVisible(stories.first().paragraphs.first().germanSentences)
            assertImageIsShown(stories.first().images.first())
        }
    }

    @Test
    fun `story reader error screenshot`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        val story = stories.first()
        storiesData.hideTranslationForStory(languageCode = "en", story = story)

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            assertErrorDialogIsShown()
        }
    }

    @Test
    fun `story title is shown`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)

        goToStoryReader(stories.first().id)
        runCurrent()

        onStoryReader {
            assertStoryTitleIsShown(stories.first().germanTitle)
        }
    }

    @Test
    fun `story content is shown`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)

        goToStoryReader(stories.last().id)
        runCurrent()

        onStoryReader {
            assertStoryTextIsVisible(stories.last().paragraphs.first().germanSentences)
        }
    }

    @Test
    fun `first image for story is shown`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)

        val story = stories.first()
        goToStoryReader(story.id)
        runCurrent()

        onStoryReader {
            assertImageIsShown(story.images.first())
        }
    }

    @Test
    fun `the story can be switched from German to English`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        goToStoryReader(stories.first().id)
        runCurrent()
        awaitIdle()

        onStoryReader {
            val paragraph = stories.first().paragraphs.first()
            val germanSentence = paragraph.germanSentences.first()
            val englishSentence = paragraph.englishSentences.first()

            tapOnSentence(sentence = germanSentence)
            awaitIdle()

            assertStoryTextIsVisible(sentence = englishSentence)
        }
    }

    @Test
    fun `the story can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            val paragraph = stories.first().paragraphs.first()
            val germanSentence = paragraph.germanSentences.first()
            val englishSentence = paragraph.englishSentences.first()

            tapOnSentence(sentence = germanSentence)
            awaitIdle()
            tapOnSentence(sentence = englishSentence)
            awaitIdle()

            assertStoryTextIsVisible(sentence = germanSentence)
        }
    }

    @Test
    fun `the story choices can be switched from German to English`() = testRule.runTest {
        // GIVEN a German story with choices is open
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        storiesData.setLocalStories(listOf(story))

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            val germanChoice = story.parts.first().choices.first().textByLanguage.getValue("de")
            val englishChoice = story.parts.first().choices.first().textByLanguage.getValue("en")

            tapOnChoiceText(text = germanChoice)
            awaitIdle()

            waitForChoiceText(text = englishChoice)
            assertChoiceIsShown(englishChoice)
        }
    }

    @Test
    fun `the story choices can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story with choices is open
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        storiesData.setLocalStories(listOf(story))

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            val choice = story.parts.first().choices.first()
            val germanChoice = choice.textByLanguage.getValue("de")
            val englishChoice = choice.textByLanguage.getValue("en")

            tapOnChoiceText(text = germanChoice)
            awaitIdle()
            waitForChoiceText(text = englishChoice)
            tapOnChoiceText(text = englishChoice)
            awaitIdle()

            waitForChoiceText(text = germanChoice)
            assertChoiceIsShown(germanChoice)
        }
    }

    @Test
    fun `the chosen choice can be switched from German to English`() = testRule.runTest {
        // GIVEN a German story with a chosen path
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        storiesData.setLocalStories(listOf(story))

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            val choice = story.parts.first().choices.first()
            val germanChoice = choice.textByLanguage.getValue("de")
            val englishChoice = choice.textByLanguage.getValue("en")

            chooseStoryOption(germanChoice)
            awaitIdle()

            waitForChoiceText(germanChoice)
            tapOnChosenChoiceText(germanChoice)
            awaitIdle()

            waitForChoiceText(englishChoice)
            assertChoiceIsShown(englishChoice)
        }
    }

    @Test
    fun `the chosen choice can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story with a chosen path
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        storiesData.setLocalStories(listOf(story))

        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            val choice = story.parts.first().choices.first()
            val germanChoice = choice.textByLanguage.getValue("de")
            val englishChoice = choice.textByLanguage.getValue("en")

            chooseStoryOption(germanChoice)
            awaitIdle()
            waitForChoiceText(germanChoice)
            tapOnChosenChoiceText(germanChoice)
            awaitIdle()
            waitForChoiceText(englishChoice)

            tapOnChosenChoiceText(englishChoice)
            awaitIdle()

            waitForChoiceText(germanChoice)
            assertChoiceIsShown(germanChoice)
        }
    }

    @Test
    fun `the title can be switched from German to English`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            tapOnSentence(sentence = stories.first().germanTitle)
            awaitIdle()

            assertStoryTitleIsShown(stories.first().englishTitle)
        }
    }

    @Test
    fun `the title can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        goToStoryReader(stories.first().id)
        awaitIdle()

        onStoryReader {
            tapOnSentence(sentence = stories.first().germanTitle)
            awaitIdle()
            tapOnSentence(sentence = stories.first().englishTitle)
            awaitIdle()

            assertStoryTitleIsShown(stories.first().germanTitle)
        }
    }

    @Test
    fun `Partially read story opens at correct position`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        val story = stories.first()
        val sentence = story.paragraphs[3].germanSentences[9]

        // GIVEN a story is open
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // AND half of the story is read
            skipToSentence(sentence = sentence)
            awaitIdle()
            // AND the story is closed
            navigateBack()
            awaitIdle()
        }

        // WHEN the story is re-opened
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the midpoint of the story is shown
            assertStoryTextIsVisible(sentence = sentence)
        }
    }

    @Test
    fun `the chosen path is remembered`() = testRule.runTest {
        val story = SampleStoriesData.chooseYourOwnAdventureStory
        storiesData.setLocalStories(listOf(story))

        val startSentence = (story.parts.first().content.first() as TestStoryPart.Paragraph).germanSentences.first()
        val keepChoiceText = story.parts.first().choices.first().textByLanguage.getValue("de")
        val returnChoiceText = story.parts.first().choices.last().textByLanguage.getValue("de")
        val newPathSentence = (story.parts.first { it.id == "keep_key" }.content.first() as TestStoryPart.Paragraph)
            .germanSentences.first()

        // GIVEN a story with a decision point
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // WHEN the first part is shown
            assertStoryTextIsVisible(startSentence)
            assertChoiceIsShown(keepChoiceText)
            assertChoiceIsShown(returnChoiceText)

            // WHEN the reader chooses to keep the key
            chooseStoryOption(keepChoiceText)
            awaitIdle()

            // THEN the story shows the chosen path
            skipToSentence(newPathSentence)
            assertStoryTextExists(newPathSentence)
            assertChoiceIsShown(keepChoiceText)
            assertChoiceIsNotShown(returnChoiceText)
        }

        // AND the story is closed
        navigateBack()
        awaitIdle()

        // WHEN the story is opened again
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            assertChoiceIsShown(keepChoiceText)
            assertChoiceIsNotShown(returnChoiceText)
            skipToSentence(newPathSentence)
            assertStoryTextExists(newPathSentence)
        }
    }

    @Test
    fun `story shows loading indicator while story loads`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        storiesData.delayStoryLoads(delayMillis = 1_000L)

        // WHEN the story reader is opened
        goToStoryReader(stories.first().id)

        onStoryReader {
            // THEN a loading indicator is displayed
            assertLoadingIndicatorIsShown()
        }
    }

    @Test
    fun `story shows error when learning story is missing`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        val story = stories.first()
        storiesData.hideStoryForLanguage(languageCode = "de", story = story)

        // GIVEN the user is reading in German with English translations
        goToStoryList()
        awaitIdle()
        onStoryList {
            setLearningLanguage("de")
            setTranslationLanguage("en")
        }

        // WHEN the story reader is opened
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the error dialog is shown and can be dismissed
            assertErrorDialogIsShown()
            dismissErrorDialog()
        }
        awaitIdle()

        onStoryList {
            assertLearningLanguageIs("de")
        }
    }

    @Test
    fun `story shows error when translation is missing`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        val story = stories.first()
        storiesData.hideTranslationForStory(languageCode = "en", story = story)

        // GIVEN a story with a missing translation
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the error dialog is shown
            assertErrorDialogIsShown()
        }
    }

    @Test
    fun `story shows error when translation sentences mismatch`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        val story = stories.first()
        storiesData.mismatchTranslationForStory(languageCode = "en", story = story)

        // GIVEN a story with mismatched translation content
        goToStoryReader(story.id)
        awaitIdle()

        onStoryReader {
            // THEN the error dialog is shown
            assertErrorDialogIsShown()
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }
}
