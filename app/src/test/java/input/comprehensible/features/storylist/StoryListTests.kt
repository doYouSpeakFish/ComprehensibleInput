package input.comprehensible.features.storylist

import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.captureScreenWithTheme
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.features.story.onStoryReader
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class StoryListTests(private val themeMode: ThemeMode) {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(themeMode)

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    fun `story list screenshot`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        goToStoryList()
        awaitIdle()

        onStoryList {
            themeMode.captureScreenWithTheme("story-list-screen")
        }
    }

    @Test
    fun `first story can be selected`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        goToStoryList()
        runCurrent()

        onStoryList {
            selectStory(stories.first())
            runCurrent()
        }

        onStoryReader {
            assertStoryTextIsVisible(stories.first().paragraphs.first().germanSentences)
        }
    }

    @Test
    fun `last story can be selected`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        goToStoryList()
        runCurrent()

        onStoryList {
            findStory(stories.lastIndex)
            selectStory(stories.last())
            runCurrent()
        }

        onStoryReader {
            assertStoryTextIsVisible(stories.last().paragraphs.first().germanSentences)
        }
    }

    @Test
    fun `images are shown for stories in the list`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)

        goToStoryList()
        runCurrent()

        onStoryList {
            assertStoryImageIsVisible(stories.first().id)
        }
    }

    @Test
    fun `stories without featured images are filtered out`() = testRule.runTest {
        // GIVEN a list of stories where one has no image available
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        val storyWithoutImage = stories.first()
        removeImagesForStory(story = storyWithoutImage)

        // WHEN the story list is opened
        goToStoryList()
        awaitIdle()

        onStoryList {
            // THEN the story missing an image does not appear, but others do
            assertStoryIsNotVisible(storyWithoutImage)
            assertStoryTitleIsVisible(stories[1].germanTitle)
        }
    }

    @Test
    fun `Story text matches learning language setting`() = testRule.runTest {
        // GIVEN a story available in German, English, and Spanish
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is shown
        goToStoryList()
        awaitIdle()

        onStoryList {
            // AND the learning language is set to German
            setLearningLanguage("de")
            awaitIdle()
            // WHEN the learning language is set to Spanish
            setLearningLanguage("es")
            awaitIdle()
            // AND a story is selected
            selectStory(stories.first(), learningLanguage = "es")
            awaitIdle()
        }

        onStoryReader {
            // THEN the story content is shown in Spanish
            assertStoryTextIsVisible(stories.first().paragraphs.first().spanishSentences)
        }
    }

    @Test
    fun `Story title shown in learning language setting`() = testRule.runTest {
        // GIVEN a story available in German, English, and Spanish
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is shown
        goToStoryList()
        awaitIdle()

        onStoryList {
            // AND the learning language is set to German
            setLearningLanguage("de")
            awaitIdle()
            // WHEN the learning language is set to Spanish
            setLearningLanguage("es")
            awaitIdle()

            // THEN the story title is shown in Spanish
            assertStoryTitleIsVisible(stories.first().spanishTitle)
        }
    }

    @Test
    fun `Translation text matches translation language setting`() = testRule.runTest {
        // GIVEN a story available in German, English, and Spanish
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is shown
        goToStoryList()
        awaitIdle()

        onStoryList {
            // AND the translation language is set to English
            setTranslationLanguage("en")
            awaitIdle()
            // WHEN the translation language is set to Spanish
            setTranslationLanguage("es")
            awaitIdle()
            // AND a story is selected
            selectStory(stories.first())
            awaitIdle()
        }

        onStoryReader {
            // AND the user taps on a sentence to translate it
            tapOnSentence(stories.first().paragraphs.first().germanSentences.first())
            awaitIdle()
            // THEN translations are shown in Spanish
            assertStoryTextIsVisible(stories.first().paragraphs.first().spanishSentences)
        }
    }

    @Test
    fun `changing the translation language keeps learning separate`() = testRule.runTest {
        // GIVEN a story available in German, English, and Spanish
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is shown
        goToStoryList()
        awaitIdle()

        onStoryList {
            // THEN the default learning and translation languages are shown
            assertLearningLanguageIs("de")
            assertTranslationLanguageIs("en")

            // WHEN the translation language is changed to German
            setTranslationLanguage("de")
            awaitIdle()

            // THEN the learning language switches to English and translations stay in German
            assertLearningLanguageIs("en")
            assertTranslationLanguageIs("de")

            // AND the reader opens the first story in English
            selectStory(stories.first(), learningLanguage = "en")
            awaitIdle()
        }

        val firstParagraph = stories.first().paragraphs.first()
        onStoryReader {
            // THEN the story is displayed in English
            assertStoryTitleIsShown(stories.first().englishTitle)
            assertStoryTextIsVisible(firstParagraph.englishSentences)

            // WHEN the reader requests a translation
            tapOnSentence(firstParagraph.englishSentences.first())
            awaitIdle()

            // THEN the translation is shown in German
            assertStoryTextIsVisible(firstParagraph.germanSentences)
        }
    }

    @Test
    fun `changing the learning language keeps translations separate`() = testRule.runTest {
        // GIVEN a story available in German, English, and Spanish
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is shown
        goToStoryList()
        awaitIdle()

        onStoryList {
            // AND the translation language is set to Spanish
            setTranslationLanguage("es")
            awaitIdle()

            // WHEN the learning language is changed to Spanish
            setLearningLanguage("es")
            awaitIdle()

            // THEN translations stay in German while learning Spanish
            assertLearningLanguageIs("es")
            assertTranslationLanguageIs("de")

            // AND the reader opens the first story in Spanish
            selectStory(stories.first(), learningLanguage = "es")
            awaitIdle()
        }

        val firstParagraph = stories.first().paragraphs.first()
        onStoryReader {
            // THEN the story is displayed in Spanish
            assertStoryTitleIsShown(stories.first().spanishTitle)
            assertStoryTextIsVisible(firstParagraph.spanishSentences)

            // WHEN the reader requests a translation
            tapOnSentence(firstParagraph.spanishSentences.first())
            awaitIdle()

            // THEN the translation is shown in German
            assertStoryTextIsVisible(firstParagraph.germanSentences)
        }
    }

    @Test
    fun `stories without translations are hidden from the list`() = testRule.runTest {
        // GIVEN two stories in the library
        val stories = SampleStoriesData.listOf100Stories.take(2)
        setLocalStories(stories)
        // AND the first story is missing an English translation
        hideTranslationForStory("en", stories.first())

        // WHEN the reader opens the story list
        goToStoryList()
        awaitIdle()

        onStoryList {
            // THEN the story without a translation is hidden
            assertStoryIsNotVisible(stories.first())
            // AND the translated story remains available
            assertStoryTitleIsVisible(stories.last().germanTitle)
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }
}
