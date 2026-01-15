package input.comprehensible

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.di.singleton.SingletonTestRule
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.robolectric.shadows.ShadowLog
import timber.log.Timber

enum class ThemeMode(
    val isDarkTheme: Boolean,
    val screenshotSuffix: String,
    private val displayName: String,
) {
    Light(isDarkTheme = false, screenshotSuffix = "light", displayName = "light"),
    Dark(isDarkTheme = true, screenshotSuffix = "dark", displayName = "dark");

    override fun toString(): String = displayName
}

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

    private val singletonTestRule = SingletonTestRule()

    override fun apply(base: Statement, description: Description): Statement {
        val testSetupStatement = object : Statement() {
            override fun evaluate() {
                Timber.plant(Timber.DebugTree())
                ShadowLog.stream = System.out
                base.evaluate()
            }
        }

        val singletonTestRuleStatement = singletonTestRule.apply(testSetupStatement, description)

        composeRule = createComposeRule(dispatcher)
        val composeRuleStatement = composeRule.apply(singletonTestRuleStatement, description)

        return roborazziRule.apply(composeRuleStatement, description)
    }
}
