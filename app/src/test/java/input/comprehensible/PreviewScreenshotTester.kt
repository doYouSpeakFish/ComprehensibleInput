package input.comprehensible

import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester.TestParameter.JUnit4TestParameter.AndroidPreviewJUnit4TestParameter
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder

@OptIn(ExperimentalRoborazziApi::class)
class PreviewScreenshotTester(
    delegate: ComposePreviewTester<AndroidPreviewJUnit4TestParameter> = createPreviewTester()
) : ComposePreviewTester<AndroidPreviewJUnit4TestParameter> by delegate

@ExperimentalRoborazziApi
fun createPreviewTester(): ComposePreviewTester<AndroidPreviewJUnit4TestParameter> = AndroidComposePreviewTester(
    capturer = { parameter ->
        val customOptions = parameter.roborazziOptions.copy(
            compareOptions = parameter.roborazziOptions.compareOptions.copy(
                // Set custom comparison threshold (0.0 = exact match, 1.0 = ignore differences)
                imageComparator = SimpleImageComparator(maxDistance = 0.05f)
            )
        )
        val preview = parameter.preview
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

        val originalPath = parameter.filePath
        val extension = originalPath.substringAfterLast(".")
        val shortPath = "screenshots/$shortId.$extension"
        AndroidComposePreviewTester.DefaultCapturer().capture(
            parameter.copy(
                roborazziOptions = customOptions,
                filePath = shortPath,
                roborazziComposeOptions = parameter.roborazziComposeOptions
            )
        )
    }
)
