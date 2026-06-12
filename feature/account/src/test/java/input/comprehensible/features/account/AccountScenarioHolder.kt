package input.comprehensible.features.account

import input.comprehensible.AccountFeatureTestScope

/**
 * Publishes the [AccountFeatureTestScope] for the scenario currently being executed so the step
 * definitions (instantiated by the Cucumber engine) can reach it. The host applies the scope
 * around each scenario; everything runs on Robolectric's single test thread, so a plain holder is
 * enough.
 */
internal object AccountScenarioHolder {
    private var current: AccountFeatureTestScope? = null

    var scope: AccountFeatureTestScope
        get() = current ?: error(
            "No active AccountFeatureTestScope. Scenarios must run through AccountFeatureCucumberTest.",
        )
        set(value) {
            current = value
        }

    fun reset() {
        current = null
    }
}
