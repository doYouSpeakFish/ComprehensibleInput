package input.comprehensible.features.softwarelicences

import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.captureScreenWithTheme
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(ParameterizedRobolectricTestRunner::class)
@HiltAndroidTest
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    application = HiltTestApplication::class,
    qualifiers = "w360dp-h640dp-mdpi",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SoftwareLicencesTest(private val themeMode: ThemeMode) {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(this, themeMode)

    @Test
    fun `about libraries software licence is shown in the list`() = testRule.runTest {
        goToSoftwareLicences()
        awaitIdle()

        onSoftwareLicences {
            assertLicenceIsVisible("AboutLibraries Core Library")
        }
    }

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    fun `software licences screenshot test`() = testRule.runTest {
        goToSoftwareLicences()
        awaitIdle()

        onSoftwareLicences {
            themeMode.captureScreenWithTheme("software-licences-screen")
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }
}
