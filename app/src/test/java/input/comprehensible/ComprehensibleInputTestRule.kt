package input.comprehensible

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import comprehensible.test.TestActivity
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.sources.FakeLanguageSettingsLocalDataSource
import input.comprehensible.data.sources.FakeStoriesInfoLocalDataSource
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.di.AppScopeProvider
import input.comprehensible.di.IoDispatcherProvider
import input.comprehensible.util.SingletonStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.robolectric.shadows.ShadowLog

enum class ThemeMode(
    val isDarkTheme: Boolean,
    val screenshotSuffix: String,
    private val displayName: String,
) {
    Light(isDarkTheme = false, screenshotSuffix = "light", displayName = "light"),
    Dark(isDarkTheme = true, screenshotSuffix = "dark", displayName = "dark");

    override fun toString(): String = displayName
}

@OptIn(ExperimentalCoroutinesApi::class)
class ComprehensibleInputTestRule(
    val themeMode: ThemeMode = ThemeMode.Light,
) : TestRule {
    val dispatcher = StandardTestDispatcher()

    lateinit var composeRule: ComposeContentTestRule
        private set

    private val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            roborazziOptions = RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(
                    changeThreshold = 0.05f,
                ),
            ),
        ),
    )

    override fun apply(base: Statement, description: Description): Statement {
        val testSetupStatement = object : Statement() {
            override fun evaluate() {
                ShadowLog.stream = System.out
                val scope = CoroutineScope(dispatcher)
                injectDependencies(dispatcher, scope)
                base.evaluate()
                scope.cancel()
                SingletonStore.clear()
            }
        }

        composeRule = createAndroidComposeRule<TestActivity>(dispatcher)
        val composeRuleStatement = composeRule.apply(testSetupStatement, description)

        return roborazziRule.apply(composeRuleStatement, description)
    }

    private fun injectDependencies(
        dispatcher: TestDispatcher,
        scope: CoroutineScope,
    ) {
        Dispatchers.setMain(dispatcher)
        IoDispatcherProvider.inject { dispatcher }
        AppScopeProvider.inject { scope }
        StoriesLocalDataSource.inject { FakeStoriesLocalDataSource() }
        LanguageSettingsLocalDataSource.inject { FakeLanguageSettingsLocalDataSource() }
        StoriesInfoLocalDataSource.inject { FakeStoriesInfoLocalDataSource() }
    }
}
