package input.comprehensible

import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.ktin.KTinTestRule
import org.junit.rules.RuleChain
import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder

@OptIn(ExperimentalRoborazziApi::class)
class PreviewScreenshotTester : ComposePreviewTester<ComposePreviewTester.TestParameter<Any>> {

    private val delegate = AndroidComposePreviewTester(
        capturer = ShortNameCapturer(),
    )

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

@OptIn(ExperimentalRoborazziApi::class)
private class ShortNameCapturer : AndroidComposePreviewTester.Capturer {
    private val defaultCapturer = AndroidComposePreviewTester.DefaultCapturer()

    override fun capture(captureParameter: AndroidComposePreviewTester.CaptureParameter) {
        val preview = captureParameter.preview
        val shortId = AndroidPreviewScreenshotIdBuilder(preview)
            .ignoreClassName()
            .ignoreIdFor("showSystemUi")
            .ignoreIdFor("device")
            .ignoreIdFor("showBackground")
            .ignoreIdFor("uiMode")
            .ignoreIdFor("fontScale")
            .ignoreIdFor("widthDp")
            .ignoreIdFor("heightDp")
            .ignoreIdFor("backgroundColor")
            .ignoreIdFor("wallpaper")
            .overrideDefaultIdFor("name") { info ->
                info.name
                    .replace(" - ", "_")
                    .replace(" ", "_")
                    .replace("%", "pct")
            }
            .build()

        val originalPath = captureParameter.filePath
        val dirPrefix = originalPath.substringBeforeLast("/")
        val extension = originalPath.substringAfterLast(".")
        val shortPath = "$dirPrefix/$shortId.$extension"

        defaultCapturer.capture(captureParameter.copy(filePath = shortPath))
    }
}
