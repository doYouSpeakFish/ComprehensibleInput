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
import input.comprehensible.data.TextAdventuresTestData
import input.comprehensible.data.UserEntity
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.DefaultAccountLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.account.sources.remote.SignInData
import input.comprehensible.data.languages.sources.DefaultLanguageSettingsLocalDataSource
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.sources.FakeTextAdventureRemoteDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import input.comprehensible.di.AppScope
import input.comprehensible.di.IoDispatcher
import input.comprehensible.ui.ComprehensibleInputApp
import input.comprehensible.ui.settings.settings.SettingsRoute
import input.comprehensible.ui.settings.softwarelicences.SoftwareLicencesRoute
import input.comprehensible.ui.storylist.StoryListRoute
import input.comprehensible.ui.storyreader.StoryReaderRoute
import input.comprehensible.util.FeatureFlags
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
    aiTextAdventuresEnabled: Boolean,
    accountManagementEnabled: Boolean,
) {
    private var isAppUiLaunched = false

    private val storiesTestData = StoriesTestData()
    private val fakeTextAdventureRemoteDataSource = FakeTextAdventureRemoteDataSource()
    private val textAdventuresTestData = TextAdventuresTestData(fakeTextAdventureRemoteDataSource)
    private val appContext = ApplicationProvider.getApplicationContext<Application>()
    private val appDb = Room
        .inMemoryDatabaseBuilder<AppDb>(context = appContext)
        .setQueryCoroutineContext(context = dispatcher)
        .build()

    private val realAccountLocalDataSource by lazy { DefaultAccountLocalDataSource(context = appContext) }

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
        FeatureFlags.inject {
            FeatureFlags(
                aiTextAdventuresEnabled = aiTextAdventuresEnabled,
                accountManagementEnabled = accountManagementEnabled,
            )
        }
        input.comprehensible.di.ApplicationProvider.inject { appContext }
        Dispatchers.setMain(dispatcher)
        IoDispatcher.inject { dispatcher }
        AppScope.inject { testScope }
        StoriesLocalDataSource.inject { FakeStoriesLocalDataSource() }
        LanguageSettingsLocalDataSource.inject {
            DefaultLanguageSettingsLocalDataSource(context = appContext)
        }
        StoriesInfoLocalDataSource.inject { appDb.getStoriesInfoDao() }
        TextAdventuresLocalDataSource.inject { appDb.getTextAdventuresDao() }
        TextAdventureRemoteDataSource.inject { fakeTextAdventureRemoteDataSource }
        AccountRemoteDataSource.inject { object : AccountRemoteDataSource {
            override suspend fun createAccount(email: String, password: String) = Unit
            override suspend fun verifyEmail(email: String, code: String) = Unit
            override suspend fun signIn(email: String, password: String) = SignInData(token = "", userId = "")
            override suspend fun signOut(token: String) = Unit
            override suspend fun requestPasswordResetCode(email: String) = Unit
            override suspend fun resetPassword(email: String, password: String, code: String) = Unit
        } }
        AccountLocalDataSource.inject { realAccountLocalDataSource }
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

    fun goToSettings() {
        navController.navigate(SettingsRoute)
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

    fun enqueueTextAdventure(
        scenario: TextAdventureRemoteResponse,
        responses: List<TextAdventureRemoteResponse>,
    ) {
        textAdventuresTestData.enqueueAdventure(scenario, responses)
    }

    suspend fun saveAccountSession(token: String, email: String, userId: String = "test-user-id") {
        appDb.getUserDao().insertUser(UserEntity(id = userId))
        realAccountLocalDataSource.saveSession(token = token, email = email, userId = userId)
    }

    suspend fun clearAccountSession() {
        realAccountLocalDataSource.clearSession()
    }

    /**
     * Navigates away from the screen under test so that it leaves the composition. Disposing the
     * screen cancels any infinite animations it hosts (such as the blinking text field cursor),
     * which would otherwise keep the test scheduler busy forever and hang [runTest]'s final drain.
     * Software licences is used as the destination because it has no infinite animations of its own.
     */
    internal suspend fun disposeUiUnderTest() {
        if (!isAppUiLaunched) return
        _navController.navigate(SoftwareLicencesRoute)
        awaitIdle()
    }

    internal fun close() {
        appDb.close()
    }
}

fun ComprehensibleInputTestRule.runTest(
    aiTextAdventuresEnabled: Boolean = true,
    accountManagementEnabled: Boolean = true,
    block: suspend ComprehensibleInputTestScope.() -> Unit
) = kotlinx.coroutines.test.runTest(context = dispatcher) {
    ComprehensibleInputTestScope(
        composeRule = composeRule,
        testScope = this,
        dispatcher = dispatcher,
        darkTheme = themeMode.isDarkTheme,
        aiTextAdventuresEnabled = aiTextAdventuresEnabled,
        accountManagementEnabled = accountManagementEnabled,
    ).apply {
        clearAccountSession()
        block()
        disposeUiUnderTest()
        close()
    }
}
