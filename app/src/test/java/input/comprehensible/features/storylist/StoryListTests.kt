package input.comprehensible.features.storylist

import android.os.Build
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.data.StoriesTestData
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.features.story.onStoryReader
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    application = HiltTestApplication::class,
    qualifiers = "w360dp-h640dp-mdpi",
)
class StoryListTests {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(this)

    @Inject
    lateinit var storiesData: StoriesTestData

    @Test
    fun `first story can be selected`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)

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
        storiesData.setLocalStories(stories)

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
        storiesData.setLocalStories(stories)

        goToStoryList()
        runCurrent()

        onStoryList {
            assertStoryImageIsVisible(stories.first().images.first().contentDescription)
        }
    }

    @Test
    fun `Story text matches learning language setting`() = testRule.runTest {
        // GIVEN a story available in German, English, and Spanish
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
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
        storiesData.setLocalStories(stories)
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
        storiesData.setLocalStories(stories)
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
    fun `Imported stories show up in the stories list`() = testRule.runTest {
        // GIVEN a story available in German, English, and Spanish
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)

        // WHEN the user imports a story
        val importedStory = SampleStoriesData.importedStory
        importStory(story = importedStory)
        awaitIdle()
        // AND the story list is shown
        goToStoryList()
        awaitIdle()

        onStoryList {
            // THEN the imported story is shown in the list
            assertStoryTitleIsVisible(importedStory.germanTitle)
        }
    }
}
