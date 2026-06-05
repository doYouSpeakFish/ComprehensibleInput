package input.comprehensible

import io.cucumber.junit.platform.engine.Constants
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.engine.discovery.DiscoverySelectors.selectUniqueId
import org.junit.platform.launcher.EngineFilter.includeEngines
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.junit.runner.Description
import org.junit.runners.model.Statement

/** A discovered Cucumber scenario. [toString] is the readable label used in test reports. */
class CucumberScenario internal constructor(
    val uniqueId: String,
    val tags: Set<String>,
    private val label: String,
) {
    override fun toString(): String = label
}

/**
 * Reusable plumbing for running Cucumber scenarios against a Robolectric Compose UI, on the
 * **Cucumber JUnit Platform engine** (not the JUnit 4 Cucumber runner). See the `:cucumberuidemo`
 * module for the full rationale.
 *
 * The Cucumber engine ignores JUnit 4 `@Rule`s, so a module provides a tiny host test driven by
 * `ParameterizedRobolectricTestRunner`, with one parameter per scenario (from [scenarios]). That
 * gives every scenario its own fresh Robolectric environment — the same per-test isolation the
 * original `@Test` methods relied on (a single shared environment would, for example, trip
 * DataStore's "multiple DataStores active for the same file" guard). The host's `@Test` then calls
 * [runScenario], which:
 *  1. Applies a fresh [ComprehensibleInputTestRule] (Light theme) — the "around each test"
 *     lifecycle the engine cannot provide for itself.
 *  2. Hands the applied rule plus an `executeSteps` callback to the host, which wraps the steps in
 *     the module's own `runTest`/test-scope and publishes that scope to its step definitions.
 *  3. Executes exactly that one scenario on the Cucumber engine and rethrows any failure.
 */
object CucumberComposeScenarios {

    /** Discovers every scenario, for use as the `@Parameters` of the host test. */
    fun scenarios(gluePackage: String, featuresPackage: String = "features"): List<CucumberScenario> =
        LauncherFactory.create()
            .discover(discoveryRequest(gluePackage, selectPackage(featuresPackage)))
            .toScenarios()

    /**
     * Runs a single scenario inside the current (per-parameter) Robolectric environment.
     *
     * @param host the host test instance, used only to label the [Description].
     * @param gluePackage the package Cucumber scans for step definitions.
     * @param scenarioId the unique id of the scenario to run (from [scenarios]).
     * @param runScenario applies the module's test scope around the scenario and invokes
     *   `executeSteps` to actually run it.
     */
    fun runScenario(
        host: Any,
        gluePackage: String,
        scenarioId: String,
        runScenario: (testRule: ComprehensibleInputTestRule, executeSteps: () -> Unit) -> Unit,
    ) {
        val testRule = ComprehensibleInputTestRule()
        val statement = object : Statement() {
            override fun evaluate() {
                runScenario(testRule) { executeScenario(gluePackage, scenarioId) }
            }
        }
        testRule.apply(statement, Description.createTestDescription(host.javaClass, scenarioId)).evaluate()
    }

    private fun executeScenario(gluePackage: String, scenarioId: String) {
        val listener = SummaryGeneratingListener()
        LauncherFactory.create().execute(discoveryRequest(gluePackage, selectUniqueId(scenarioId)), listener)

        val summary = listener.summary
        if (summary.totalFailureCount > 0) {
            val failure = summary.failures.first()
            throw AssertionError(
                "Cucumber scenario failed: ${failure.testIdentifier.displayName}",
                failure.exception,
            )
        }
    }

    private fun discoveryRequest(gluePackage: String, selector: DiscoverySelector) =
        request()
            .selectors(selector)
            .filters(includeEngines("cucumber"))
            .configurationParameter(Constants.GLUE_PROPERTY_NAME, gluePackage)
            .configurationParameter(Constants.PLUGIN_PROPERTY_NAME, "pretty")
            .build()

    private fun TestPlan.toScenarios(): List<CucumberScenario> {
        val scenarios = mutableListOf<CucumberScenario>()
        fun visit(identifier: TestIdentifier, parentLabel: String) {
            val children = getChildren(identifier)
            if (children.isEmpty()) {
                if (identifier.isTest) {
                    val label = if (parentLabel.isEmpty()) identifier.displayName else "$parentLabel – ${identifier.displayName}"
                    val tags = identifier.tags.map { it.name.removePrefix("@") }.toSet()
                    scenarios += CucumberScenario(identifier.uniqueId, tags, label)
                }
            } else {
                children.forEach { visit(it, identifier.displayName) }
            }
        }
        roots.forEach { visit(it, "") }
        return scenarios
    }
}
