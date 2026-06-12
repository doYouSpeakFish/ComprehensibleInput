package input.comprehensible.features

import android.app.Application
import android.os.Build
import input.comprehensible.CucumberComposeScenarios
import input.comprehensible.CucumberScenario
import input.comprehensible.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * JUnit 4 host that runs every app Cucumber scenario against a Robolectric Compose UI.
 *
 * `ParameterizedRobolectricTestRunner` gives each scenario its own fresh Robolectric environment;
 * [CucumberComposeScenarios] supplies the per-scenario
 * [input.comprehensible.ComprehensibleInputTestRule]; and [runTest] supplies the coroutine test
 * scope, the in-memory database and the fakes the existing tests relied on. Feature flags that a
 * few scenarios need are expressed as Cucumber tags and read from [CucumberScenario.tags].
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
    application = Application::class,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppFeatureCucumberTest(private val scenario: CucumberScenario) {

    @Test
    fun scenario() = CucumberComposeScenarios.runScenario(
        host = this,
        gluePackage = GLUE_PACKAGE,
        scenarioId = scenario.uniqueId,
    ) { testRule, executeSteps ->
        testRule.runTest(
            aiTextAdventuresEnabled = AI_TEXT_ADVENTURES_DISABLED !in scenario.tags,
            accountManagementEnabled = ACCOUNT_MANAGEMENT_DISABLED !in scenario.tags,
        ) {
            AppScenarioHolder.scope = this
            try {
                executeSteps()
            } finally {
                AppScenarioHolder.reset()
            }
        }
    }

    companion object {
        private const val GLUE_PACKAGE = "input.comprehensible.features"
        private const val AI_TEXT_ADVENTURES_DISABLED = "aiTextAdventuresDisabled"
        private const val ACCOUNT_MANAGEMENT_DISABLED = "accountManagementDisabled"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun scenarios() = CucumberComposeScenarios.scenarios(GLUE_PACKAGE).map { arrayOf(it) }
    }
}
