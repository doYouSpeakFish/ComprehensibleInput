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
            assertStoryTitleIsShown(stories.first().title)
        }
    }

    @Test
    fun `story content is shown`() = testRule.runTest {
        val stories = SampleStoriesData.listOf100Stories
        storiesData.setLocalStories(stories)

        goToStoryReader(stories.last().id)
        runCurrent()

        onStoryReader {
            assertStoryTextIsVisible(stories.last().paragraphs.first().text)
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
}
