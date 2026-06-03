package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.AccountFeatureTestScope
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.delayAccountRequests
import input.comprehensible.enqueueRequestPasswordResetCodeResult
import input.comprehensible.onAccount
import input.comprehensible.onForgotPassword
import input.comprehensible.onPasswordReset
import input.comprehensible.runAccountFeatureTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE],
    qualifiers = "w360dp-h640dp-mdpi",
    application = Application::class,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@OptIn(ExperimentalRoborazziApi::class)
class ForgotPasswordTests(private val themeMode: ThemeMode) {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(themeMode)

    companion object {
        @Suppress("unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }

    @Test
    fun `forgot password button is visible on sign in screen`() = testRule.runAccountFeatureTest {
        // GIVEN the account screen is open showing the sign in step
        goToAccount()
        awaitIdle()

        // WHEN the sign in screen is shown
        // THEN the forgot password button is visible
        onAccount { tapForgotPassword() }
        awaitIdle()

        onForgotPassword {
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `forgot password submit button is disabled when email is empty`() = testRule.runAccountFeatureTest {
        // GIVEN the forgot password screen is shown
        goToForgotPassword()
        awaitIdle()

        onForgotPassword {
            // THEN the submit button is disabled when no email has been entered
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `forgot password submit button is disabled when email is invalid`() = testRule.runAccountFeatureTest {
        // GIVEN the forgot password screen is shown
        goToForgotPassword()
        awaitIdle()

        onForgotPassword {
            // WHEN an email without an @ symbol is entered
            enterEmail("invalidemail")

            // THEN the submit button is disabled
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `forgot password submit button is enabled when email is valid`() = testRule.runAccountFeatureTest {
        // GIVEN the forgot password screen is shown
        goToForgotPassword()
        awaitIdle()

        onForgotPassword {
            // WHEN a valid email is entered
            enterEmail("user@example.com")

            // THEN the submit button is enabled
            assertSubmitIsEnabled()
        }
    }

    @Test
    fun `forgot password shows loading state while request is in progress`() = testRule.runAccountFeatureTest {
        // GIVEN the forgot password screen is shown with a valid email entered
        goToForgotPassword()
        awaitIdle()
        onForgotPassword { enterEmail("user@example.com") }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueRequestPasswordResetCodeResult(Result.success(Unit))

        // WHEN the submit button is tapped
        onForgotPassword { tapSubmit() }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onForgotPassword {
            assertLoadingIndicatorIsShown()
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `forgot password navigates to password reset screen on success`() = testRule.runAccountFeatureTest {
        // GIVEN the forgot password screen is shown with a valid email entered
        goToForgotPassword()
        awaitIdle()
        onForgotPassword { enterEmail("user@example.com") }
        enqueueRequestPasswordResetCodeResult(Result.success(Unit))

        // WHEN the request succeeds
        onForgotPassword { tapSubmit() }
        awaitIdle()

        // THEN the password reset screen is shown with the submitted email address
        onPasswordReset {
            assertResetCodeMessageIsShown("user@example.com")
        }
    }

    @Test
    fun `forgot password shows error dialog on failure`() = testRule.runAccountFeatureTest {
        // GIVEN the forgot password screen is shown with a valid email entered
        goToForgotPassword()
        awaitIdle()
        onForgotPassword { enterEmail("user@example.com") }
        enqueueRequestPasswordResetCodeResult(Result.failure<Unit>(Exception("Network error")))

        // WHEN the request fails
        onForgotPassword { tapSubmit() }
        awaitIdle()

        // THEN the error dialog is shown
        onForgotPassword {
            errorDialog.assertIsShown()
        }
    }

    @Test
    fun `forgot password error dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN a forgot password error has occurred
        goToForgotPassword()
        awaitIdle()
        onForgotPassword { enterEmail("user@example.com") }
        enqueueRequestPasswordResetCodeResult(Result.failure<Unit>(Exception("Network error")))
        onForgotPassword { tapSubmit() }
        awaitIdle()
        onForgotPassword { errorDialog.assertIsShown() }

        // WHEN the error dialog is dismissed
        onForgotPassword { errorDialog.dismiss() }
        awaitIdle()

        // THEN the forgot password form is restored and the submit button is enabled
        onForgotPassword {
            assertSubmitIsEnabled()
        }
    }
}
