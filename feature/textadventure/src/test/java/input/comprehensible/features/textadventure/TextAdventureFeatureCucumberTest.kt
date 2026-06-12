package input.comprehensible.features.textadventure

import android.app.Application
import android.os.Build
import input.comprehensible.CucumberComposeScenarios
import input.comprehensible.CucumberScenario
import input.comprehensible.runTextAdventureFeatureTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * JUnit 4 host that runs every text-adventure-feature Cucumber scenario against a Robolectric
 * Compose UI, mirroring the account feature's host. `ParameterizedRobolectricTestRunner` gives each
 * scenario its own fresh Robolectric environment; [CucumberComposeScenarios] supplies the
 * per-scenario rule and [runTextAdventureFeatureTest] the coroutine scope and fakes, published to
 * the step definitions through [TextAdventureScenarioHolder].
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
    application = Application::class,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TextAdventureFeatureCucumberTest(private val scenario: CucumberScenario) {

    @Test
    fun scenario() = CucumberComposeScenarios.runScenario(
        host = this,
        gluePackage = GLUE_PACKAGE,
        scenarioId = scenario.uniqueId,
    ) { testRule, executeSteps ->
        testRule.runTextAdventureFeatureTest {
            TextAdventureScenarioHolder.scope = this
            try {
                executeSteps()
            } finally {
                TextAdventureScenarioHolder.reset()
            }
        }
    }

    companion object {
        private const val GLUE_PACKAGE = "input.comprehensible.features.textadventure"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun scenarios() = CucumberComposeScenarios.scenarios(GLUE_PACKAGE).map { arrayOf(it) }
    }
}
