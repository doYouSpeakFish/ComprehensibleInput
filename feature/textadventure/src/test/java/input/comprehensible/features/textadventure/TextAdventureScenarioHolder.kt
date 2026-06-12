package input.comprehensible.features.textadventure

import input.comprehensible.TextAdventureFeatureTestScope

/**
 * Publishes the [TextAdventureFeatureTestScope] for the scenario currently being executed so the
 * step definitions (instantiated by the Cucumber engine) can reach it. Everything runs on
 * Robolectric's single test thread, so a plain holder is enough.
 */
internal object TextAdventureScenarioHolder {
    private var current: TextAdventureFeatureTestScope? = null

    var scope: TextAdventureFeatureTestScope
        get() = current ?: error(
            "No active TextAdventureFeatureTestScope. " +
                "Scenarios must run through TextAdventureFeatureCucumberTest.",
        )
        set(value) {
            current = value
        }

    fun reset() {
        current = null
    }
}
