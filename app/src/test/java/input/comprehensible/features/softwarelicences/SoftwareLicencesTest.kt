package input.comprehensible.features.softwarelicences

import android.os.Build
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    application = HiltTestApplication::class,
)
class SoftwareLicencesTest {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(this)

    @Test
    fun `about libraries software licence is shown in the list`() = testRule.runTest {
        goToSoftwareLicences()
        runCurrent()

        onSoftwareLicences {
            assertLicenceIsVisible("AboutLibraries Core Library")
        }
    }
}