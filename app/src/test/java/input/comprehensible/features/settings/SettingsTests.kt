package input.comprehensible.features.settings

import android.os.Build
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.features.softwarelicences.onSoftwareLicences
import input.comprehensible.features.storylist.onStoryList
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SettingsTests {
    @get:Rule
    val testRule = ComprehensibleInputTestRule()

    @Test
    fun `settings screen shows software licences option`() = testRule.runTest {
        // GIVEN a full library of stories is available for readers
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is open
        goToStoryList()
        awaitIdle()

        onStoryList {
            // WHEN the reader opens the settings screen
            openSettings()
        }
        awaitIdle()

        onSettings {
            // THEN the settings title is visible
            assertSettingsTitleIsVisible()
            // AND the software licences link is shown
            assertSoftwareLicencesOptionIsVisible()
        }
    }

    @Test
    fun `software licences link opens the software licences screen`() = testRule.runTest {
        // GIVEN a full library of stories is available for readers
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is open
        goToStoryList()
        awaitIdle()

        onStoryList {
            // AND the reader opens the settings screen
            openSettings()
        }
        awaitIdle()

        onSettings {
            // WHEN the reader chooses the software licences link
            openSoftwareLicences()
        }
        awaitIdle()

        onSoftwareLicences {
            // THEN the software licences screen title is visible
            assertSoftwareLicencesTitleIsVisible()
        }
    }

    @Test
    fun `navigate up returns to the story list`() = testRule.runTest {
        // GIVEN a full library of stories is available for readers
        val stories = SampleStoriesData.listOf100Stories
        setLocalStories(stories)
        // AND the story list is open
        goToStoryList()
        awaitIdle()

        onStoryList {
            // AND the reader opens the settings screen
            openSettings()
        }
        awaitIdle()

        onSettings {
            // WHEN the reader goes back
            navigateBackToStories()
        }
        awaitIdle()

        onStoryList {
            // THEN the story list is shown again
            assertLearningLanguageIs("de")
        }
    }
}
