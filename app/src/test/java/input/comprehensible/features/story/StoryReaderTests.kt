package input.comprehensible.features.story

import android.os.Build
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import comprehensible.test.TestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import input.comprehensible.data.StoriesTestData
import input.comprehensible.ui.storyreader.StoryReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    application = HiltTestApplication::class,
)
class StoryReaderTests {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule(order = 0)
    val hiltAndroidRule = HiltAndroidRule(this)

    @OptIn(ExperimentalTestApi::class)
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>(testDispatcher)

    @Inject
    lateinit var storiesData: StoriesTestData

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        ShadowLog.stream = System.out
        hiltAndroidRule.inject()
    }

    @Test
    fun `story title is shown`() = runTest {
        storiesData.setLocalStory(
            title = "Test Story",
            content = "First line of the story"
        )
        composeTestRule.setContent { StoryReader() }
        runCurrent()
        storyReaderRobot(composeTestRule).assertStoryTitleIsShown("Test Story")
    }

    @Test
    fun `story content is shown`() = runTest {
        storiesData.setLocalStory(
            title = "Test Story",
            content = "First line of the story"
        )
        composeTestRule.setContent { StoryReader() }
        runCurrent()
        storyReaderRobot(composeTestRule).assertStoryLineIsVisible("First line of the story")
    }
}
