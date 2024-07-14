package input.comprehensible.features.story

import android.os.Build
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.data.StoriesTestData
import input.comprehensible.data.sample.SampleStoriesData
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
)
class StoryReaderTests {

    @get:Rule
    val testRule = ComprehensibleInputTestRule(this)

    @Inject
    lateinit var storiesData: StoriesTestData

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
            assertStoryTextIsVisible(stories.last().paragraphs.first().germanText)
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

        onStoryReader {
            // WHEN the user switches to English
            setLanguage("en")
            runCurrent()

            // THEN the story is shown in English
            assertStoryTextIsVisible(stories.last().paragraphs.first().englishText)
        }
    }

    @Test
    fun `the story can be switched from English to German`() = testRule.runTest {
        // GIVEN a German story is open
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)
        goToStoryReader(stories.first().id)
        runCurrent()

        onStoryReader {
            // WHEN the user switches to English
            setLanguage("en")
            runCurrent()
            // AND the user switches to German
            setLanguage("de")
            runCurrent()

            // THEN the story is shown in German
            assertStoryTextIsVisible(stories.last().paragraphs.first().germanText)
        }
    }
}
