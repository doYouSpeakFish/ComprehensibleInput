package input.comprehensible.features

import input.comprehensible.ComprehensibleInputTestScope

/**
 * Publishes the [ComprehensibleInputTestScope] for the scenario currently being executed so the
 * app step definitions can reach it. The host applies the scope around each scenario; everything
 * runs on Robolectric's single test thread, so a plain holder is enough.
 */
internal object AppScenarioHolder {
    private var current: ComprehensibleInputTestScope? = null

    var scope: ComprehensibleInputTestScope
        get() = current ?: error(
            "No active ComprehensibleInputTestScope. Scenarios must run through AppFeatureCucumberTest.",
        )
        set(value) {
            current = value
        }

    fun reset() {
        current = null
    }
}
