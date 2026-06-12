package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import input.comprehensible.CucumberComposeScenarios
import input.comprehensible.CucumberScenario
import input.comprehensible.runAccountFeatureTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * JUnit 4 host that runs every account-feature Cucumber scenario against a Robolectric Compose UI.
 *
 * `ParameterizedRobolectricTestRunner` gives each scenario its own fresh Robolectric environment
 * (the per-test isolation the original `@Test` methods relied on);
 * [CucumberComposeScenarios] supplies the per-scenario
 * [input.comprehensible.ComprehensibleInputTestRule]; and [runAccountFeatureTest] supplies the
 * coroutine test scope and fakes the existing tests relied on. The active scope is published
 * through [AccountScenarioHolder] for the step definitions.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
    application = Application::class,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AccountFeatureCucumberTest(private val scenario: CucumberScenario) {

    @Test
    fun scenario() = CucumberComposeScenarios.runScenario(
        host = this,
        gluePackage = GLUE_PACKAGE,
        scenarioId = scenario.uniqueId,
    ) { testRule, executeSteps ->
        testRule.runAccountFeatureTest {
            AccountScenarioHolder.scope = this
            try {
                executeSteps()
            } finally {
                AccountScenarioHolder.reset()
            }
        }
    }

    companion object {
        private const val GLUE_PACKAGE = "input.comprehensible.features.account"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun scenarios() = CucumberComposeScenarios.scenarios(GLUE_PACKAGE).map { arrayOf(it) }
    }
}
