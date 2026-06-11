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
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.DefaultAccountLocalDataSource
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.languagesettings.fakes.FakeLanguageSettingsLocalDataSource
import input.comprehensible.data.languagesettings.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.sources.FakeAccountRemoteDataSource
import input.comprehensible.data.textadventure.AdventureMessageSender
import input.comprehensible.data.textadventure.fakes.FakeAdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.local.AdventureEntity
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.local.MessageEntity
import input.comprehensible.data.textadventure.sources.local.SentenceEntity
import input.comprehensible.data.textadventure.sources.remote.AdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.RemoteAdventure
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import input.comprehensible.data.user.UserEntity
import input.comprehensible.di.AppScope
import input.comprehensible.di.IoDispatcher
import input.comprehensible.test.textadventure.TextAdventureTestDatabase
import input.comprehensible.ui.textadventure.TextAdventuresListRoute
import input.comprehensible.ui.textadventure.textAdventureNavGraph
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
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
 * text adventure data sources the screens exercise. The local store is the real
 * [AdventureLocalDataSource] backed by an in-memory [TextAdventureTestDatabase], so the tests run
 * against Room rather than a hand-written fake; only the remote boundary is faked. The list screen
 * is hosted in a `NavHost` with a placeholder standing in for the account screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TextAdventureFeatureTestScope(
    private val composeContentRule: ComposeContentTestRule,
    val testScope: TestScope,
    private val dispatcher: CoroutineDispatcher,
    private val darkTheme: Boolean,
) {
    val fakeRemoteDataSource = FakeAdventureRemoteDataSource()
    private val appContext = ApplicationProvider.getApplicationContext<Application>()
    private val db = Room
        .inMemoryDatabaseBuilder<TextAdventureTestDatabase>(context = appContext)
        .setQueryCoroutineContext(context = dispatcher)
        .build()
    private val realAccountLocalDataSource by lazy { DefaultAccountLocalDataSource(context = appContext) }
    private var currentEmail = "user@example.com"

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
        UserLocalDataSource.inject { db.getUserDao() }
        AdventureRemoteDataSource.inject { fakeRemoteDataSource }
        AdventureLocalDataSource.inject { db.getAdventureDao() }
        LanguageSettingsLocalDataSource.inject { FakeLanguageSettingsLocalDataSource() }
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
                        onCreateAccountClick = { navController.navigate(TestAccountRoute) },
                        onSettingsClick = {},
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
        currentEmail = email
        testScope.launch {
            realAccountLocalDataSource.saveSession(
                token = "test-token",
                email = email,
                userId = userIdFor(email),
            )
            seedUser(email)
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

    fun returnAdventureWithImage(title: String) {
        fakeRemoteDataSource.adventures = listOf(
            remoteAdventure(title).copy(imageUrl = "https://images.test/$title.webp"),
        )
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

    fun failRestore() {
        fakeRemoteDataSource.failRestoreAdventure = true
    }

    fun failStart() {
        fakeRemoteDataSource.failStartAdventure = true
    }

    fun rateLimitRefresh() {
        fakeRemoteDataSource.rateLimitGetAdventures = true
    }

    fun rateLimitStart() {
        fakeRemoteDataSource.rateLimitStartAdventure = true
    }

    fun rateLimitUserMessage() {
        fakeRemoteDataSource.rateLimitSendUserMessage = true
    }

    fun rateLimitAiMessage() {
        fakeRemoteDataSource.rateLimitGenerateAiMessage = true
    }

    fun startReturns(text: String, translation: String) {
        fakeRemoteDataSource.startResponse = TextAdventureRemoteResponse(
            messageId = "message-1",
            adventureId = "adventure-1",
            title = "Adventure",
            sentences = listOf(text),
            translatedSentences = listOf(translation),
            isEnding = false,
        )
    }

    fun startReturnsWithImage(text: String) {
        fakeRemoteDataSource.startResponse = TextAdventureRemoteResponse(
            messageId = "message-1",
            adventureId = "adventure-1",
            title = "Adventure",
            sentences = listOf(text),
            translatedSentences = listOf("$text (translated)"),
            isEnding = false,
            // An arbitrary catalogue-style id; tests assert only that an image is shown, not which.
            imageId = "cover-1",
        )
    }

    fun startReturnsLongPassage() {
        val sentences = (1..LONG_PASSAGE_SENTENCES).map { "Opening sentence number $it of a long passage." }
        fakeRemoteDataSource.startResponse = TextAdventureRemoteResponse(
            messageId = "message-1",
            adventureId = "adventure-1",
            title = "Adventure",
            sentences = sentences,
            translatedSentences = sentences.map { "$it (translated)" },
            isEnding = false,
        )
    }

    fun delayUserMessage() {
        fakeRemoteDataSource.userMessageDelayMillis = IN_FLIGHT_DELAY_MILLIS
    }

    fun delayAiMessage() {
        fakeRemoteDataSource.aiMessageDelayMillis = IN_FLIGHT_DELAY_MILLIS
    }

    fun failUserMessage() {
        fakeRemoteDataSource.failSendUserMessage = true
    }

    fun failAiMessage() {
        fakeRemoteDataSource.failGenerateAiMessage = true
    }

    fun userMessageReturnsTranslation(text: String, translation: String) {
        fakeRemoteDataSource.userMessageResponse = FakeAdventureRemoteDataSource.messageResponse(
            id = "user-message-1",
            type = "user",
            text = text,
            translation = translation,
        )
    }

    fun aiRespondsWith(text: String, isEnding: Boolean = false) {
        fakeRemoteDataSource.aiMessageResponse = FakeAdventureRemoteDataSource.messageResponse(
            id = "ai-message-1",
            type = "AI",
            text = text,
            translation = "$text (translated)",
            isEnding = isEnding,
        )
    }

    fun cacheAdventure(title: String, email: String) {
        testScope.launch {
            seedUser(email)
            db.getAdventureDao().upsertAdventure(adventureEntity(title, email))
        }
        testScope.advanceUntilIdle()
    }

    fun adventureRefreshesTo(title: String, text: String) {
        fakeRemoteDataSource.messagesResponse = fakeRemoteDataSource.messagesResponse.copy(
            adventureId = title,
            messages = listOf(
                FakeAdventureRemoteDataSource.messageResponse(
                    id = "refreshed-message-1",
                    type = "AI",
                    text = text,
                    translation = "$text (translated)",
                ),
            ),
        )
    }

    fun cacheAdventureWithMessage(title: String, message: String) {
        val messageId = "$title-message"
        testScope.launch {
            seedUser(currentEmail)
            db.getAdventureDao().upsertAdventure(adventureEntity(title, currentEmail))
            db.getAdventureDao().upsertMessage(
                MessageEntity(
                    id = messageId,
                    adventureId = title,
                    parentId = null,
                    sender = AdventureMessageSender.AI.name,
                    isEnding = false,
                    position = 0,
                ),
            )
            db.getAdventureDao().insertSentences(
                listOf(
                    SentenceEntity(
                        messageId = messageId,
                        paragraphIndex = 0,
                        sentenceIndex = 0,
                        text = message,
                        translation = "$message (translated)",
                    ),
                ),
            )
        }
        testScope.advanceUntilIdle()
    }

    fun idle() {
        testScope.runCurrent()
        composeContentRule.waitForIdle()
    }

    fun advanceTime(millis: Long) {
        testScope.advanceTimeBy(millis)
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

    internal fun close() {
        db.close()
    }

    /**
     * Seeds the [UserEntity] for [email] so adventures owned by that user satisfy the
     * `Adventure.userId` foreign key in the in-memory database. Idempotent (an upsert), so it is safe
     * to call for each adventure seeded for a user.
     */
    private suspend fun seedUser(email: String) {
        db.getUserDao().upsertUser(UserEntity(id = userIdFor(email), email = email))
    }

    private fun adventureEntity(title: String, email: String) = AdventureEntity(
        id = title,
        userId = userIdFor(email),
        title = title,
        translatedTitle = "$title (translated)",
        learningLanguage = "de",
        translationLanguage = "en",
        updatedAt = 0L,
    )

    private fun remoteAdventure(title: String) = RemoteAdventure(
        id = title,
        title = title,
        translatedTitle = "$title (translated)",
        learningLanguage = "de",
        translationLanguage = "en",
        updatedAt = 0L,
    )

    private fun userIdFor(email: String) = "user-id-$email"

    private companion object {
        const val IN_FLIGHT_DELAY_MILLIS = 60_000L
        const val LONG_PASSAGE_SENTENCES = 40
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
        close()
    }
}
