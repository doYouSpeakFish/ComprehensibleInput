package input.comprehensible.cucumber

import input.comprehensible.ComprehensibleInputTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Rule
import io.cucumber.java.After
import io.cucumber.java.Before

@OptIn(ExperimentalCoroutinesApi::class)
class CucumberHooks {
    @get:Rule
    val testRule = ComprehensibleInputTestRule()

    @Before
    fun beforeScenario() {
        CucumberTestScopeSingleton.inject {
            CucumberComprehensibleInputTestScope(
                composeRule = testRule.composeRule,
                testScope = TestScope(testRule.dispatcher),
                dispatcher = testRule.dispatcher,
                darkTheme = testRule.themeMode.isDarkTheme,
                aiTextAdventuresEnabled = true,
            )
        }
    }

    @After
    fun afterScenario() {
        CucumberTestScopeSingleton().close()
    }
}
