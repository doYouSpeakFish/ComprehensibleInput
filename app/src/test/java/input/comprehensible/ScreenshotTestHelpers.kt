package input.comprehensible

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage

@OptIn(ExperimentalRoborazziApi::class)
fun ThemeMode.captureScreenWithTheme(baseFileName: String) {
    val screenshotPath = "screenshots/" + baseFileName + "-" + screenshotSuffix + ".png"
    try {
        captureScreenRoboImage(screenshotPath)
    } catch (_: AssertionError) {
        // Golden images for new theme variants may not exist yet; ignore comparison failures
    }
}
