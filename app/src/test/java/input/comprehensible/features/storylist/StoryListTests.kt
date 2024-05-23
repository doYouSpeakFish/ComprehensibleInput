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
            assertStoryLineIsVisible(stories.first().content)
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
            assertStoryLineIsVisible(stories.last().content)
        }
    }
}
