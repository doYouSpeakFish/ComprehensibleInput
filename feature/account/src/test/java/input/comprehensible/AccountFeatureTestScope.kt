package input.comprehensible

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import input.comprehensible.data.account.AccountRepository
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.DefaultAccountLocalDataSource
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.sources.FakeAccountRemoteDataSource
import input.comprehensible.data.user.UserEntity
import input.comprehensible.di.AppScope
import input.comprehensible.di.IoDispatcher
import input.comprehensible.test.account.AccountTestDatabase
import input.comprehensible.ui.settings.account.AccountRoute
import input.comprehensible.ui.settings.account.DeleteAccountRoute
import input.comprehensible.ui.settings.account.ForgotPasswordRoute
import input.comprehensible.ui.settings.account.PasswordResetRoute
import input.comprehensible.ui.settings.account.SignUpRoute
import input.comprehensible.ui.settings.account.VerifyEmailRoute
import input.comprehensible.ui.settings.account.accountNavGraph
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.Serializable

@Serializable
internal data object TestDisposeRoute

@OptIn(ExperimentalCoroutinesApi::class)
class AccountFeatureTestScope(
    private val composeContentRule: ComposeContentTestRule,
    val testScope: TestScope,
    private val dispatcher: CoroutineDispatcher,
    private val darkTheme: Boolean,
) {
    val fakeAccountRemoteDataSource = FakeAccountRemoteDataSource()
    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    // The real Room-backed user DAO, kept in an in-memory database, wrapped so a test can force a
    // save to fail (see FailableUserLocalDataSource). All other reads and writes hit real Room.
    private val db = Room
        .inMemoryDatabaseBuilder<AccountTestDatabase>(context = appContext)
        .setQueryCoroutineContext(context = dispatcher)
        .build()
    val userLocalDataSource = FailableUserLocalDataSource(delegate = db.getUserDao())
    val realAccountLocalDataSource by lazy { DefaultAccountLocalDataSource(context = appContext) }

    private lateinit var _navController: TestNavHostController
    private var _isLaunched = false

    val composeRule: ComposeContentTestRule
        get() {
            if (!_isLaunched) launch()
            return composeContentRule
        }

    init {
        input.comprehensible.di.ApplicationProvider.inject { appContext }
        Dispatchers.setMain(dispatcher)
        IoDispatcher.inject { dispatcher }
        AppScope.inject { testScope }
        AccountRemoteDataSource.inject { fakeAccountRemoteDataSource }
        AccountLocalDataSource.inject { realAccountLocalDataSource }
        UserLocalDataSource.inject { userLocalDataSource }
    }

    fun launch() {
        composeContentRule.setContent {
            _navController = TestNavHostController(LocalContext.current)
            _navController.navigatorProvider.addNavigator(ComposeNavigator())
            ComprehensibleInputTheme(darkTheme = darkTheme) {
                NavHost(
                    navController = _navController,
                    startDestination = AccountRoute,
                ) {
                    accountNavGraph(navController = _navController)
                    composable<TestDisposeRoute> {}
                }
            }
        }
        testScope.runCurrent()
        _isLaunched = true
    }

    suspend fun signInAs(email: String, userId: String = "test-user-id") {
        realAccountLocalDataSource.saveSession(token = "test-token", email = email, userId = userId)
        userLocalDataSource.upsertUser(UserEntity(id = userId, email = email))
    }

    /**
     * Non-suspending variant of [signInAs] for Cucumber step definitions, which run outside a
     * coroutine. The session is persisted on the injected (test) dispatcher, so draining the
     * scheduler completes the write before the UI under test reads it.
     */
    fun signInAsBlocking(email: String, userId: String = "test-user-id") {
        testScope.launch { signInAs(email, userId) }
        testScope.advanceUntilIdle()
    }

    /** The current user exposed by the account repository, drained on the test scheduler. */
    fun currentUser(): UserEntity? {
        var user: UserEntity? = null
        testScope.launch { user = AccountRepository().user.first() }
        testScope.advanceUntilIdle()
        return user
    }

    /** Whether a local user record with [id] exists in the database, drained on the test scheduler. */
    fun userRecordExists(id: String): Boolean {
        var exists = false
        testScope.launch { exists = userLocalDataSource.getUser(id) != null }
        testScope.advanceUntilIdle()
        return exists
    }

    fun goToAccount() {
        if (!_isLaunched) launch()
        _navController.navigate(AccountRoute)
    }

    fun goToSignUp() {
        if (!_isLaunched) launch()
        _navController.navigate(SignUpRoute)
    }

    fun goToVerifyEmail(email: String) {
        if (!_isLaunched) launch()
        _navController.navigate(VerifyEmailRoute(email))
    }

    fun goToDeleteAccount() {
        if (!_isLaunched) launch()
        _navController.navigate(DeleteAccountRoute)
    }

    fun goToForgotPassword() {
        if (!_isLaunched) launch()
        _navController.navigate(ForgotPasswordRoute)
    }

    fun goToPasswordReset(email: String) {
        if (!_isLaunched) launch()
        _navController.navigate(PasswordResetRoute(email))
    }

    fun runCurrent() {
        testScope.runCurrent()
    }

    suspend fun awaitIdle() {
        testScope.runCurrent()
        composeRule.awaitIdle()
    }

    /**
     * Non-suspending equivalent of [awaitIdle] for Cucumber step definitions: pumps the test
     * scheduler and then the Compose/Robolectric main looper so the UI reflects pending work.
     */
    fun idle() {
        testScope.runCurrent()
        composeRule.waitForIdle()
    }

    /**
     * Advances the virtual clock by [durationMillis], running any work scheduled in that window, and
     * pumps the UI. Lets Cucumber steps drive time-based behaviour such as the resend-code cooldown
     * countdown deterministically.
     */
    fun advanceTimeBy(durationMillis: Long) {
        testScope.advanceTimeBy(durationMillis)
        testScope.runCurrent()
        composeRule.waitForIdle()
    }

    /**
     * Runs all remaining scheduled work to completion (e.g. lets an in-progress cooldown countdown
     * finish) and pumps the UI.
     */
    fun advanceUntilIdle() {
        testScope.advanceUntilIdle()
        composeRule.waitForIdle()
    }

    suspend fun clearAccountSession() {
        realAccountLocalDataSource.clearSession()
    }

    internal suspend fun disposeUiUnderTest() {
        if (!_isLaunched) return
        _navController.navigate(TestDisposeRoute)
        awaitIdle()
    }

    internal fun close() {
        db.close()
    }
}

fun ComprehensibleInputTestRule.runAccountFeatureTest(
    block: suspend AccountFeatureTestScope.() -> Unit,
) = kotlinx.coroutines.test.runTest(context = dispatcher) {
    AccountFeatureTestScope(
        composeContentRule = composeRule,
        testScope = this,
        dispatcher = dispatcher,
        darkTheme = themeMode.isDarkTheme,
    ).apply {
        clearAccountSession()
        block()
        disposeUiUnderTest()
        close()
    }
}

fun AccountFeatureTestScope.delayAccountRequests(delayMillis: Long) {
    fakeAccountRemoteDataSource.requestDelayMillis = delayMillis
}

fun AccountFeatureTestScope.enqueueSignInResult(result: Result<String>) {
    fakeAccountRemoteDataSource.enqueueSignInResult(result)
}

fun AccountFeatureTestScope.enqueueCreateAccountResult(result: Result<Unit>) {
    fakeAccountRemoteDataSource.enqueueCreateAccountResult(result)
}

fun AccountFeatureTestScope.enqueueVerifyEmailResult(result: Result<Unit>) {
    fakeAccountRemoteDataSource.enqueueVerifyEmailResult(result)
}

fun AccountFeatureTestScope.enqueueRequestEmailVerificationCodeResult(result: Result<Unit>) {
    fakeAccountRemoteDataSource.enqueueRequestEmailVerificationCodeResult(result)
}

fun AccountFeatureTestScope.enqueueDeleteAccountResult(result: Result<Unit>) {
    fakeAccountRemoteDataSource.enqueueDeleteAccountResult(result)
}

fun AccountFeatureTestScope.enqueueRequestPasswordResetCodeResult(result: Result<Unit>) {
    fakeAccountRemoteDataSource.enqueueRequestPasswordResetCodeResult(result)
}

fun AccountFeatureTestScope.enqueueResetPasswordResult(result: Result<Unit>) {
    fakeAccountRemoteDataSource.enqueueResetPasswordResult(result)
}
