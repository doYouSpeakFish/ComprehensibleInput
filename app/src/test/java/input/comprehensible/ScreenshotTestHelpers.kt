package input.comprehensible

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage

@OptIn(ExperimentalRoborazziApi::class)
fun ThemeMode.captureScreenWithTheme(baseFileName: String) {
    captureScreenRoboImage(filePath = "screenshots/$baseFileName-$screenshotSuffix.png")
}
