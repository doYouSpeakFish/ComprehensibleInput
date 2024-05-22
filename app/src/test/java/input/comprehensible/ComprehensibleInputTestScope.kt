package input.comprehensible

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import input.comprehensible.ui.ComprehensibleInputApp
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

    fun launchAppUi() {
        if (!isAppUiLaunched) {
            composeRule.setContent {
                navController = TestNavHostController(LocalContext.current)
                navController.navigatorProvider.addNavigator(ComposeNavigator())
                ComprehensibleInputApp()
            }
            testScope.runCurrent()
            isAppUiLaunched = true
        }
    }

    fun goToStoryList() {
        launchAppUi()
        navController.navigate("storyList")
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
