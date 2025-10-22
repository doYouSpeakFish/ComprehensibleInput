package input.comprehensible.features.story

import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.captureScreenWithTheme
import input.comprehensible.data.StoriesTestData
import input.comprehensible.data.sample.SampleStoriesData
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
            themeMode.captureScreenWithTheme("story-reader-screen")
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
            themeMode.captureScreenWithTheme("story-reader-error")
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
            // WHEN the user taps on a German sentence
            tapOnSentence(sentence = stories.first().paragraphs.first().germanSentences.first())
            awaitIdle()

            // THEN the sentence is shown in English
            themeMode.captureScreenWithTheme("german_sentence_to_english")
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
            // WHEN the user taps on a German sentence
            tapOnSentence(sentence = stories.first().paragraphs.first().germanSentences.first())
            awaitIdle()
            // WHEN the user taps on an English sentence
            tapOnSentence(sentence = stories.first().paragraphs.first().englishSentences.first())
            awaitIdle()

            // THEN the sentence is shown in German
            themeMode.captureScreenWithTheme("english_sentence_to_german")
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
            // WHEN the user taps on the title
            tapOnSentence(sentence = stories.first().germanTitle)
            awaitIdle()

            // THEN the title is shown in English
            themeMode.captureScreenWithTheme("german_title_to_english")
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
            // WHEN the user taps on the title
            tapOnSentence(sentence = stories.first().germanTitle)
            awaitIdle()
            // WHEN the user taps on the title
            tapOnSentence(sentence = stories.first().englishTitle)
            awaitIdle()

            // THEN the title is shown in German
            themeMode.captureScreenWithTheme("english_title_to_german")
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
