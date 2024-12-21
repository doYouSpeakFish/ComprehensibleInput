package input.comprehensible

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sample.toJson
import input.comprehensible.ui.ComprehensibleInputApp
import input.comprehensible.ui.settings.settings.SettingsRoute
import input.comprehensible.ui.settings.softwarelicences.SoftwareLicencesRoute
import input.comprehensible.ui.storylist.StoryListRoute
import input.comprehensible.ui.storyreader.StoryReaderRoute
import input.comprehensible.util.Document
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent

@OptIn(ExperimentalCoroutinesApi::class)
class ComprehensibleInputTestScope(
    val composeRule: ComposeContentTestRule,
    val testScope: TestScope
) {
    private var isAppUiLaunched = false
    private lateinit var navController: TestNavHostController
    private var importedDocument: Document? = null

    // TODO: Create a custom composition local provider for importing text files to the app.
    //  Return a TextFile interface, that can have concrete implementations for different platforms.
    //  Pass the TextFile up to the data layer, which actually reads the file, parses it, stores
    //  it in the database, and returns the parsed data.
    //  In tests, set a fake of the composition local provider, that returns a fake TextFile.

    fun launchAppUi() {
        if (!isAppUiLaunched) {
            composeRule.setContent {
                navController = TestNavHostController(LocalContext.current)
                navController.navigatorProvider.addNavigator(ComposeNavigator())
                ComprehensibleInputApp(navController = navController)
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

    fun goToSettings() {
        launchAppUi()
        navController.navigate(SettingsRoute)
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

    fun importStory(story: TestStory) {
        importedDocument = object : Document {
            override suspend fun readText() = story.toJson()
        }
        goToSettings()
        composeRule.onNodeWithContentDescription("Import a story").performClick()
    }
}

fun ComprehensibleInputTestRule.runTest(
    block: suspend ComprehensibleInputTestScope.() -> Unit
) = kotlinx.coroutines.test.runTest {
    ComprehensibleInputTestScope(
        composeRule = composeRule,
        testScope = this
    ).block()
}
