package input.comprehensible.cucumberuidemo

import android.app.Application
import android.os.Build
import androidx.compose.ui.test.junit4.createComposeRule
import io.cucumber.junit.platform.engine.Constants
import org.junit.Test
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.engine.discovery.DiscoverySelectors.selectUniqueId
import org.junit.platform.launcher.EngineFilter.includeEngines
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Demonstrates running Cucumber scenarios against a Robolectric Compose UI **without** the
 * Cucumber JUnit 4 runner.
 *
 * The hard part: the Cucumber JUnit Platform engine does not honour JUnit 4 `@Rule`s, so the
 * Compose test rule (and the Robolectric sandbox a Compose-under-Robolectric test needs) never
 * get set up if scenarios are launched the usual way via `@Suite`.
 *
 * The solution here:
 *  1. A JUnit 4 host test provides the Robolectric sandbox via [RobolectricTestRunner]. Because
 *     everything below runs inside this test method, the thread context class loader is the
 *     Robolectric sandbox loader, so the Cucumber engine, the glue classes and the Compose code
 *     they touch are all loaded with Android shadows in place.
 *  2. The Cucumber JUnit Platform engine is driven programmatically through the JUnit Platform
 *     [Launcher]. Each scenario is executed individually so it can be wrapped in its own freshly
 *     applied Compose rule (a [Statement]) — exactly the "around each test" behaviour a JUnit 4
 *     runner would normally give a `@Rule`.
 *  3. The active Compose rule is shared with the step definitions through [ComposeRuleHolder].
 */
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
    application = Application::class,
)
class CucumberComposeUiTest {

    @Test
    fun runsCucumberScenariosWithComposeRule() {
        val launcher = LauncherFactory.create()
        val testPlan = launcher.discover(discoveryRequest(selectPackage(FEATURES_PACKAGE)))
        val scenarioIds = testPlan.leafTestIds()

        check(scenarioIds.isNotEmpty()) { "No Cucumber scenarios were discovered." }

        scenarioIds.forEach { scenarioId -> runScenarioWithComposeRule(launcher, scenarioId) }
    }

    private fun runScenarioWithComposeRule(launcher: Launcher, scenarioId: String) {
        val composeRule = createComposeRule()
        val scenarioStatement = object : Statement() {
            override fun evaluate() {
                ComposeRuleHolder.composeRule = composeRule
                try {
                    executeScenario(launcher, scenarioId)
                } finally {
                    ComposeRuleHolder.reset()
                }
            }
        }
        val description = Description.createTestDescription(javaClass, scenarioId)
        composeRule.apply(scenarioStatement, description).evaluate()
    }

    private fun executeScenario(launcher: Launcher, scenarioId: String) {
        val listener = SummaryGeneratingListener()
        launcher.execute(discoveryRequest(selectUniqueId(scenarioId)), listener)

        val summary = listener.summary
        if (summary.totalFailureCount > 0) {
            val failure = summary.failures.first()
            throw AssertionError(
                "Cucumber scenario failed: ${failure.testIdentifier.displayName}",
                failure.exception,
            )
        }
    }

    private fun discoveryRequest(selector: DiscoverySelector) =
        request()
            .selectors(selector)
            .filters(includeEngines("cucumber"))
            .configurationParameter(Constants.GLUE_PROPERTY_NAME, GLUE_PACKAGE)
            .configurationParameter(Constants.PLUGIN_PROPERTY_NAME, "pretty")
            .build()

    private fun TestPlan.leafTestIds(): List<String> {
        val ids = mutableListOf<String>()
        fun visit(identifier: TestIdentifier) {
            val children = getChildren(identifier)
            if (children.isEmpty()) {
                if (identifier.isTest) ids += identifier.uniqueId
            } else {
                children.forEach(::visit)
            }
        }
        roots.forEach(::visit)
        return ids
    }

    private companion object {
        const val FEATURES_PACKAGE = "features"
        const val GLUE_PACKAGE = "input.comprehensible.cucumberuidemo"
    }
}
