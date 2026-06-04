package input.comprehensible.cucumberuidemo

import androidx.compose.ui.test.junit4.ComposeContentTestRule

/**
 * Bridges the Compose test rule (owned and applied by the JUnit 4 host runner) into the
 * Cucumber step definitions (instantiated by the Cucumber JUnit Platform engine).
 *
 * The Cucumber engine does not process JUnit 4 `@Rule`s, so the rule cannot live on a step
 * definition class. Instead the host applies the rule around each scenario and publishes the
 * active instance here. Everything runs on Robolectric's single test thread, so a plain holder
 * is sufficient.
 */
internal object ComposeRuleHolder {
    private var current: ComposeContentTestRule? = null

    var composeRule: ComposeContentTestRule
        get() = current ?: error(
            "No active ComposeContentTestRule. A scenario must be run through CucumberComposeUiTest.",
        )
        set(value) {
            current = value
        }

    fun reset() {
        current = null
    }
}
