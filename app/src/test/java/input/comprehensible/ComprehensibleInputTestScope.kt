package input.comprehensible

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import input.comprehensible.data.AppDb
import input.comprehensible.data.StoriesTestData
import input.comprehensible.data.languages.sources.DefaultLanguageSettingsLocalDataSource
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.di.AppScope
import input.comprehensible.di.IoDispatcher
import input.comprehensible.ui.ComprehensibleInputApp
import input.comprehensible.ui.settings.softwarelicences.SoftwareLicencesRoute
import input.comprehensible.ui.storylist.StoryListRoute
import input.comprehensible.ui.storyreader.StoryReaderRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ComprehensibleInputTestScope(
    composeRule: ComposeContentTestRule,
    private val dispatcher: CoroutineDispatcher,
    val testScope: TestScope,
    private val darkTheme: Boolean,
) {
    private var isAppUiLaunched = false

    private val storiesTestData = StoriesTestData()
    private val appContext = ApplicationProvider.getApplicationContext<Application>()
    private val appDb = Room
        .inMemoryDatabaseBuilder<AppDb>(context = appContext)
        .setQueryCoroutineContext(context = dispatcher)
        .build()

    private lateinit var _navController: TestNavHostController
    private val navController: TestNavHostController
        get() {
            if (!isAppUiLaunched) launchAppUi()
            return _navController
        }

    private val _composeRule = composeRule
    val composeRule: ComposeContentTestRule
        get() {
            if (!isAppUiLaunched) launchAppUi()
            return _composeRule
        }

    init {
        input.comprehensible.di.ApplicationProvider.inject { appContext }
        Dispatchers.setMain(dispatcher)
        IoDispatcher.inject { dispatcher }
        AppScope.inject { testScope }
        StoriesLocalDataSource.inject { FakeStoriesLocalDataSource() }
        LanguageSettingsLocalDataSource.inject {
            DefaultLanguageSettingsLocalDataSource(context = appContext)
        }
        StoriesInfoLocalDataSource.inject { appDb.getStoriesInfoDao() }
    }

    fun launchAppUi() {
        _composeRule.setContent {
            _navController = TestNavHostController(LocalContext.current)
            _navController.navigatorProvider.addNavigator(ComposeNavigator())
            ComprehensibleInputApp(
                navController = _navController,
                darkTheme = darkTheme,
            )
        }
        testScope.runCurrent()
        isAppUiLaunched = true
    }

    fun goToStoryList() {
        navController.navigate(StoryListRoute)
    }

    fun goToStoryReader(id: String) {
        navController.navigate(StoryReaderRoute(storyId = id))
    }

    fun goToSoftwareLicences() {
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

    internal fun close() {
        appDb.close()
    }
}

fun ComprehensibleInputTestRule.runTest(
    block: suspend ComprehensibleInputTestScope.() -> Unit
) = kotlinx.coroutines.test.runTest(context = dispatcher) {
    ComprehensibleInputTestScope(
        composeRule = composeRule,
        testScope = this,
        dispatcher = dispatcher,
        darkTheme = themeMode.isDarkTheme,
    ).apply {
        block()
        close()
    }
}
