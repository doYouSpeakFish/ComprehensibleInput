package input.comprehensible

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import comprehensible.test.TestActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.robolectric.shadows.ShadowLog
import javax.inject.Singleton

private val standardTestDispatcher = StandardTestDispatcher()

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class ComprehensibleInputTestRule(private val testInstance: Any) : TestRule {
    val testDispatcher = standardTestDispatcher

    lateinit var composeRule: ComposeContentTestRule
        private set

    lateinit var hiltAndroidRule: HiltAndroidRule
        private set

    private val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            roborazziOptions = RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(
                    changeThreshold = 0.01f,
                ),
            ),
        ),
    )

    override fun apply(base: Statement, description: Description): Statement {

        val testSetupStatement = object : Statement() {
            override fun evaluate() {
                Dispatchers.setMain(testDispatcher)
                ShadowLog.stream = System.out
                hiltAndroidRule.inject()
                base.evaluate()
            }
        }

        composeRule = createAndroidComposeRule<TestActivity>(testDispatcher)
        val composeRuleStatement = composeRule.apply(testSetupStatement, description)

        hiltAndroidRule = HiltAndroidRule(testInstance)
        val hiltRuleStatement = hiltAndroidRule.apply(composeRuleStatement, description)

        return roborazziRule.apply(hiltRuleStatement, description)
    }
}

@Module
@InstallIn(SingletonComponent::class)
class CoroutinesModule {
    @Provides
    @Singleton
    fun provideTestDispatcher(): TestDispatcher = standardTestDispatcher
}
