package input.comprehensible

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.DefaultAccountLocalDataSource
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.sources.FakeAccountRemoteDataSource
import input.comprehensible.data.sources.FakeUserLocalDataSource
import input.comprehensible.data.textadventure.fakes.FakeAdventureLocalDataSource
import input.comprehensible.data.textadventure.fakes.FakeAdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.local.AdventureEntity
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.remote.AdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.RemoteAdventure
import input.comprehensible.di.AppScope
import input.comprehensible.di.IoDispatcher
import input.comprehensible.ui.textadventure.TextAdventuresListRoute
import input.comprehensible.ui.textadventure.textAdventureNavGraph
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.Serializable

@Serializable
internal data object TestAccountRoute

@Serializable
internal data object TestDisposeRoute

/**
 * Per-scenario environment for the text adventures feature tests. Injects the account dependencies
 * (a real DataStore session plus account fakes) needed to construct the account repository, and the
 * text adventure fakes the screen actually exercises. The list screen is hosted in a `NavHost` with
 * placeholder destinations standing in for the account screen and the (not-yet-built) chat screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TextAdventureFeatureTestScope(
    private val composeContentRule: ComposeContentTestRule,
    val testScope: TestScope,
    private val dispatcher: CoroutineDispatcher,
    private val darkTheme: Boolean,
) {
    val fakeRemoteDataSource = FakeAdventureRemoteDataSource()
    val fakeLocalDataSource = FakeAdventureLocalDataSource()
    private val appContext = ApplicationProvider.getApplicationContext<Application>()
    private val realAccountLocalDataSource by lazy { DefaultAccountLocalDataSource(context = appContext) }

    private lateinit var navController: TestNavHostController
    private var isLaunched = false

    val composeRule: ComposeContentTestRule
        get() {
            if (!isLaunched) launch()
            return composeContentRule
        }

    init {
        input.comprehensible.di.ApplicationProvider.inject { appContext }
        Dispatchers.setMain(dispatcher)
        IoDispatcher.inject { dispatcher }
        AppScope.inject { testScope }
        AccountRemoteDataSource.inject { FakeAccountRemoteDataSource() }
        AccountLocalDataSource.inject { realAccountLocalDataSource }
        UserLocalDataSource.inject { FakeUserLocalDataSource() }
        AdventureRemoteDataSource.inject { fakeRemoteDataSource }
        AdventureLocalDataSource.inject { fakeLocalDataSource }
    }

    private fun launch() {
        composeContentRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            ComprehensibleInputTheme(darkTheme = darkTheme) {
                NavHost(navController = navController, startDestination = TextAdventuresListRoute) {
                    textAdventureNavGraph(
                        navController = navController,
                        onSignInClick = { navController.navigate(TestAccountRoute) },
                    )
                    composable<TestAccountRoute> {
                        Box(Modifier.fillMaxSize().testTag("account_screen"))
                    }
                    composable<TestDisposeRoute> {}
                }
            }
        }
        testScope.runCurrent()
        isLaunched = true
    }

    fun openScreen() {
        if (!isLaunched) launch()
        idle()
    }

    fun signInAs(email: String) {
        testScope.launch {
            realAccountLocalDataSource.saveSession(
                token = "test-token",
                email = email,
                userId = userIdFor(email),
            )
        }
        testScope.advanceUntilIdle()
    }

    fun signOut() {
        testScope.launch { realAccountLocalDataSource.clearSession() }
        testScope.advanceUntilIdle()
    }

    fun returnAdventures(vararg titles: String) {
        fakeRemoteDataSource.adventures = titles.map(::remoteAdventure)
    }

    fun delayRequests() {
        fakeRemoteDataSource.requestDelayMillis = IN_FLIGHT_DELAY_MILLIS
    }

    fun failRefresh() {
        fakeRemoteDataSource.failGetAdventures = true
    }

    fun failDelete() {
        fakeRemoteDataSource.failDeleteAdventure = true
    }

    fun cacheAdventure(title: String, email: String) {
        fakeLocalDataSource.seed(
            AdventureEntity(
                id = title,
                userId = userIdFor(email),
                title = title,
                learningLanguage = "German",
                translationLanguage = "English",
                updatedAt = 0L,
            ),
        )
    }

    fun idle() {
        testScope.runCurrent()
        composeContentRule.waitForIdle()
    }

    suspend fun clearAccountSession() {
        realAccountLocalDataSource.clearSession()
    }

    internal suspend fun disposeUiUnderTest() {
        if (!isLaunched) return
        navController.navigate(TestDisposeRoute)
        testScope.runCurrent()
        composeContentRule.awaitIdle()
    }

    private fun remoteAdventure(title: String) = RemoteAdventure(
        id = title,
        title = title,
        learningLanguage = "German",
        translationLanguage = "English",
        updatedAt = 0L,
    )

    private fun userIdFor(email: String) = "user-id-$email"

    private companion object {
        const val IN_FLIGHT_DELAY_MILLIS = 1_000L
    }
}

fun ComprehensibleInputTestRule.runTextAdventureFeatureTest(
    block: suspend TextAdventureFeatureTestScope.() -> Unit,
) = kotlinx.coroutines.test.runTest(context = dispatcher) {
    TextAdventureFeatureTestScope(
        composeContentRule = composeRule,
        testScope = this,
        dispatcher = dispatcher,
        darkTheme = themeMode.isDarkTheme,
    ).apply {
        clearAccountSession()
        block()
        disposeUiUnderTest()
    }
}
