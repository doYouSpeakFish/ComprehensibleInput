package input.comprehensible

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import input.comprehensible.data.StoriesTestData
import input.comprehensible.data.sample.TestStory
import input.comprehensible.ui.ComprehensibleInputApp
import input.comprehensible.ui.settings.softwarelicences.SoftwareLicencesRoute
import input.comprehensible.ui.storylist.StoryListRoute
import input.comprehensible.ui.storyreader.StoryReaderRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent

@OptIn(ExperimentalCoroutinesApi::class)
class ComprehensibleInputTestScope(
    val composeRule: ComposeContentTestRule,
    val testScope: TestScope,
    private val darkTheme: Boolean,
) {
    private var isAppUiLaunched = false
    private lateinit var navController: TestNavHostController
    private val storiesTestData = StoriesTestData()

    fun launchAppUi() {
        if (!isAppUiLaunched) {
            composeRule.setContent {
                navController = TestNavHostController(LocalContext.current)
                navController.navigatorProvider.addNavigator(ComposeNavigator())
                ComprehensibleInputApp(
                    navController = navController,
                    darkTheme = darkTheme,
                )
            }
            testScope.runCurrent()
            isAppUiLaunched = true
        }
    }

    fun goToStoryList() {
        launchAppUi()
        navController.navigate(StoryListRoute)
    }

    fun goToStoryReader(id: String) {
        launchAppUi()
        navController.navigate(StoryReaderRoute(storyId = id))
    }

    fun goToSoftwareLicences() {
        launchAppUi()
        navController.navigate(SoftwareLicencesRoute)
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    fun runCurrent() {
        testScope.runCurrent()
    }

    suspend fun awaitIdle() {
        testScope.runCurrent()
        composeRule.awaitIdle()
    }

    fun setLocalStories(stories: List<TestStory>) {
        storiesTestData.setLocalStories(stories)
    }

    fun removeImagesForStory(story: TestStory) {
        storiesTestData.removeImagesForStory(story)
    }

    fun hideTranslationForStory(languageCode: String, story: TestStory) {
        storiesTestData.hideTranslationForStory(languageCode, story)
    }

    fun delayStoryLoads(delayMillis: Long) {
        storiesTestData.delayStoryLoads(delayMillis)
    }

    fun mismatchTranslationForStory(languageCode: String, story: TestStory) {
        storiesTestData.mismatchTranslationForStory(languageCode, story)
    }

    fun hideStoryForLanguage(languageCode: String, story: TestStory) {
        storiesTestData.hideStoryForLanguage(languageCode, story)
    }
}

fun ComprehensibleInputTestRule.runTest(
    block: suspend ComprehensibleInputTestScope.() -> Unit
) = kotlinx.coroutines.test.runTest {
    ComprehensibleInputTestScope(
        composeRule = composeRule,
        testScope = this,
        darkTheme = themeMode.isDarkTheme,
    ).block()
}
