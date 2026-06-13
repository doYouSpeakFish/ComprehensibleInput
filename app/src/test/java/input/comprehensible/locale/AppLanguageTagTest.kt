package input.comprehensible.locale

import android.app.Application
import com.ktin.KTinTestRule
import input.comprehensible.di.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Verifies that [appLanguageTag] reports the BCP-47 tag of the locale Android actually resolves the
 * app's strings to. The `@Config(qualifiers = ...)` annotation makes Robolectric load the matching
 * `values-*` resources, mirroring what happens when the app runs in that locale.
 *
 * A plain [Application] is used (rather than the project's `App`) so each test gets a clean context
 * without re-running the app's one-shot dependency injection.
 */
@RunWith(RobolectricTestRunner::class)
class AppLanguageTagTest {

    // Resets the injected singletons around each test so ApplicationProvider can be set safely.
    @get:Rule
    val singletonRule = KTinTestRule()

    @Test
    @Config(application = Application::class)
    fun `defaults to the injected application context and reports English`() {
        // GIVEN the application context available through the app's provider, in the default locale
        ApplicationProvider.inject { RuntimeEnvironment.getApplication() }

        // WHEN the app language tag is read without passing a context explicitly
        val tag = appLanguageTag()

        // THEN it resolves against the app resources and reports English
        assertEquals("en", tag)
    }

    @Test
    @Config(application = Application::class, qualifiers = "de")
    fun `reports German when the app is displayed in German`() {
        // GIVEN the app displayed with German resources
        // WHEN the app language tag is read
        val tag = appLanguageTag(RuntimeEnvironment.getApplication())

        // THEN it reports German
        assertEquals("de", tag)
    }

    @Test
    @Config(application = Application::class, qualifiers = "in")
    fun `reports the modern Indonesian tag for the values-in folder`() {
        // GIVEN the app displayed with Indonesian resources (Android's legacy values-in folder)
        // WHEN the app language tag is read
        val tag = appLanguageTag(RuntimeEnvironment.getApplication())

        // THEN it reports the modern BCP-47 tag "id" rather than the legacy "in"
        assertEquals("id", tag)
    }
}
