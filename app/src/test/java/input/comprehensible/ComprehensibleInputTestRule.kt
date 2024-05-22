package input.comprehensible

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import comprehensible.test.TestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.robolectric.shadows.ShadowLog

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class ComprehensibleInputTestRule : TestRule {
    val testDispatcher = StandardTestDispatcher()

    lateinit var composeRule: ComposeContentTestRule
        private set

    override fun apply(base: Statement, description: Description): Statement {
        val hiltAndroidRule = HiltAndroidRule(this)
        val hiltRuleStatement = hiltAndroidRule.apply(base, description)

        composeRule = createAndroidComposeRule<TestActivity>(testDispatcher)
        val composeRuleStatement = composeRule.apply(hiltRuleStatement, description)

        Dispatchers.setMain(testDispatcher)
        ShadowLog.stream = System.out
        hiltAndroidRule.inject()

        return composeRuleStatement
    }
}