package input.comprehensible

import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.ktin.KTinTestRule
import org.junit.rules.RuleChain

@OptIn(ExperimentalRoborazziApi::class)
class PreviewScreenshotTester : ComposePreviewTester<ComposePreviewTester.TestParameter<Any>> {

    private val delegate = AndroidComposePreviewTester()

    override fun options(): ComposePreviewTester.Options {
        val delegateOptions = delegate.options()
        val delegateLifecycle =
            delegateOptions.testLifecycleOptions as ComposePreviewTester.Options.JUnit4TestLifecycleOptions
        return delegateOptions.copy(
            testLifecycleOptions = delegateLifecycle.copy(
                testRuleFactory = { composeTestRule ->
                    RuleChain
                        .outerRule(KTinTestRule())
                        .around(delegateLifecycle.testRuleFactory(composeTestRule))
                },
            ),
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun testParameters(): List<ComposePreviewTester.TestParameter<Any>> =
        delegate.testParameters() as List<ComposePreviewTester.TestParameter<Any>>

    @Suppress("UNCHECKED_CAST")
    override fun test(testParameter: ComposePreviewTester.TestParameter<Any>) {
        delegate.test(testParameter as ComposePreviewTester.TestParameter.JUnit4TestParameter.AndroidPreviewJUnit4TestParameter)
    }
}
