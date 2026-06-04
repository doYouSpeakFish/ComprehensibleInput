package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.AccountFeatureTestScope
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.data.account.InvalidResetCodeException
import input.comprehensible.delayAccountRequests
import input.comprehensible.enqueueResetPasswordResult
import input.comprehensible.onAccount
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
class PasswordResetTests(private val themeMode: ThemeMode) {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(themeMode)

    companion object {
        @Suppress("unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }

    @Test
    fun `password reset submit button is disabled when fields are empty`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown
        goToPasswordReset("user@example.com")
        awaitIdle()

        onPasswordReset {
            // THEN the submit button is disabled when no fields have been filled
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `password reset submit button is disabled when code is too short`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown
        goToPasswordReset("user@example.com")
        awaitIdle()

        onPasswordReset {
            // WHEN a code with fewer than 6 digits is entered with valid passwords
            enterResetCode("123")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")

            // THEN the submit button is disabled
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `password reset submit button is disabled when password is too short`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown
        goToPasswordReset("user@example.com")
        awaitIdle()

        onPasswordReset {
            // WHEN a password shorter than 12 characters is entered
            enterResetCode("123456")
            enterNewPassword("short")
            enterConfirmNewPassword("short")

            // THEN the submit button is disabled
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `password reset submit button is disabled when passwords do not match`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown
        goToPasswordReset("user@example.com")
        awaitIdle()

        onPasswordReset {
            // WHEN a valid code and password are entered but the confirm password does not match
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("differentpassword12345")

            // THEN the submit button is disabled
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `password reset submit button is enabled when all fields are valid`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown
        goToPasswordReset("user@example.com")
        awaitIdle()

        onPasswordReset {
            // WHEN a 6-digit code and matching passwords of sufficient length are entered
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")

            // THEN the submit button is enabled
            assertSubmitIsEnabled()
        }
    }

    @Test
    fun `password reset shows loading state while request is in progress`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown with all fields filled
        goToPasswordReset("user@example.com")
        awaitIdle()
        onPasswordReset {
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")
        }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueResetPasswordResult(Result.success(Unit))

        // WHEN the submit button is tapped
        onPasswordReset { tapSubmit() }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onPasswordReset {
            assertLoadingIndicatorIsShown()
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `password reset navigates to account screen on success`() = testRule.runAccountFeatureTest {
        // GIVEN the account screen is open and the password reset screen is navigated to
        goToAccount()
        awaitIdle()
        goToPasswordReset("user@example.com")
        awaitIdle()
        onPasswordReset {
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")
        }
        enqueueResetPasswordResult(Result.success(Unit))

        // WHEN the reset request succeeds
        onPasswordReset { tapSubmit() }
        awaitIdle()

        // THEN the account sign in screen is shown
        onAccount {
            assertSignInScreenIsShown()
        }
    }

    @Test
    fun `password reset shows error dialog on failure`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown with all fields filled
        goToPasswordReset("user@example.com")
        awaitIdle()
        onPasswordReset {
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")
        }
        enqueueResetPasswordResult(Result.failure<Unit>(Exception("Invalid code")))

        // WHEN the reset request fails
        onPasswordReset { tapSubmit() }
        awaitIdle()

        // THEN the error dialog is shown
        onPasswordReset {
            errorDialog.assertIsShown()
        }
    }

    @Test
    fun `password reset error dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN a password reset error has occurred
        goToPasswordReset("user@example.com")
        awaitIdle()
        onPasswordReset {
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")
        }
        enqueueResetPasswordResult(Result.failure<Unit>(Exception("Invalid code")))
        onPasswordReset { tapSubmit() }
        awaitIdle()
        onPasswordReset { errorDialog.assertIsShown() }

        // WHEN the error dialog is dismissed
        onPasswordReset { errorDialog.dismiss() }
        awaitIdle()

        // THEN the password reset form is restored and the submit button is enabled
        onPasswordReset {
            assertSubmitIsEnabled()
        }
    }

    @Test
    fun `password reset shows invalid code dialog when reset code is invalid or expired`() = testRule.runAccountFeatureTest {
        // GIVEN the password reset screen is shown with all fields filled
        goToPasswordReset("user@example.com")
        awaitIdle()
        onPasswordReset {
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")
        }
        enqueueResetPasswordResult(Result.failure<Unit>(InvalidResetCodeException()))

        // WHEN the reset request fails with an invalid code
        onPasswordReset { tapSubmit() }
        awaitIdle()

        // THEN the invalid code dialog is shown instead of the generic error dialog
        onPasswordReset {
            invalidCodeErrorDialog.assertIsShown()
        }
    }

    @Test
    fun `password reset invalid code dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN an invalid code error has occurred
        goToPasswordReset("user@example.com")
        awaitIdle()
        onPasswordReset {
            enterResetCode("123456")
            enterNewPassword("newpassword12345")
            enterConfirmNewPassword("newpassword12345")
        }
        enqueueResetPasswordResult(Result.failure<Unit>(InvalidResetCodeException()))
        onPasswordReset { tapSubmit() }
        awaitIdle()
        onPasswordReset { invalidCodeErrorDialog.assertIsShown() }

        // WHEN the invalid code dialog is dismissed
        onPasswordReset { invalidCodeErrorDialog.dismiss() }
        awaitIdle()

        // THEN the password reset form is restored and the submit button is enabled
        onPasswordReset {
            assertSubmitIsEnabled()
        }
    }
}
