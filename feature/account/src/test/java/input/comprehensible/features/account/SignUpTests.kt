package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.AccountFeatureTestScope
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.delayAccountRequests
import input.comprehensible.enqueueCreateAccountResult
import input.comprehensible.enqueueVerifyEmailResult
import input.comprehensible.onAccount
import input.comprehensible.onSignUp
import input.comprehensible.onVerifyEmail
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
class SignUpTests(private val themeMode: ThemeMode) {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(themeMode)

    companion object {
        @Suppress("unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }

    // Sign up tests

    @Test
    fun `sign up button is disabled when fields are empty`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown
        goToSignUp()
        awaitIdle()

        onSignUp {
            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when email is missing`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown
        goToSignUp()
        awaitIdle()

        onSignUp {
            // WHEN password and confirm password are filled but email is not entered
            enterPassword("password12345")
            enterConfirmPassword("password12345")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when email is invalid`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown
        goToSignUp()
        awaitIdle()

        onSignUp {
            // WHEN an email without an @ symbol is entered
            enterEmail("invalidemail")
            enterPassword("password12345")
            enterConfirmPassword("password12345")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when password is too short`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown
        goToSignUp()
        awaitIdle()

        onSignUp {
            // WHEN a password shorter than 12 characters is entered
            enterEmail("user@example.com")
            enterPassword("short")
            enterConfirmPassword("short")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when passwords do not match`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown
        goToSignUp()
        awaitIdle()

        onSignUp {
            // WHEN a valid email and password are entered but the confirm password does not match
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("differentpassword12345")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is enabled when all fields are valid`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown
        goToSignUp()
        awaitIdle()

        onSignUp {
            // WHEN all fields are filled with valid values and passwords match
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")

            // THEN the submit button is enabled
            assertSignUpSubmitIsEnabled()
        }
    }

    @Test
    fun `sign up shows loading state while request is in progress`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown with valid fields filled
        goToSignUp()
        awaitIdle()
        onSignUp {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueCreateAccountResult(Result.success(Unit))

        // WHEN the sign up button is tapped
        onSignUp {
            tapSignUpSubmit()
        }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onSignUp {
            assertSignUpLoadingIndicatorIsShown()
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up navigates to verify email screen on success`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown with valid fields filled
        goToSignUp()
        awaitIdle()
        onSignUp {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))

        // WHEN the sign up request succeeds
        onSignUp {
            tapSignUpSubmit()
        }
        awaitIdle()

        // THEN the email verification screen is shown with the submitted email address
        onVerifyEmail {
            assertEmailSentMessageIsShown("user@example.com")
        }
    }

    @Test
    fun `sign up shows error dialog on failure`() = testRule.runAccountFeatureTest {
        // GIVEN the sign up screen is shown with valid fields filled
        goToSignUp()
        awaitIdle()
        onSignUp {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.failure<Unit>(Exception("Network error")))

        // WHEN the sign up request fails
        onSignUp {
            tapSignUpSubmit()
        }
        awaitIdle()

        // THEN the error dialog is shown
        onSignUp {
            errorDialog.assertIsShown()
        }
    }

    @Test
    fun `sign up error dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN a sign up error has occurred
        goToSignUp()
        awaitIdle()
        onSignUp {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.failure<Unit>(Exception("Network error")))
        onSignUp { tapSignUpSubmit() }
        awaitIdle()
        onSignUp { errorDialog.assertIsShown() }

        // WHEN the error dialog is dismissed
        onSignUp { errorDialog.dismiss() }
        awaitIdle()

        // THEN the sign up form is restored and the submit button is enabled
        onSignUp {
            assertSignUpSubmitIsEnabled()
        }
    }

    // Verify email tests

    @Test
    fun `verify email button is disabled when code is empty`() = testRule.runAccountFeatureTest {
        // GIVEN the verify email screen is shown
        goToVerifyEmail("user@example.com")
        awaitIdle()

        onVerifyEmail {
            // THEN the verify button is disabled when no code has been entered
            assertVerifyEmailSubmitIsDisabled()
        }
    }

    @Test
    fun `verify email button is disabled when code is less than 6 digits`() = testRule.runAccountFeatureTest {
        // GIVEN the verify email screen is shown
        goToVerifyEmail("user@example.com")
        awaitIdle()

        onVerifyEmail {
            // WHEN a code with fewer than 6 digits is entered
            enterVerificationCode("123")

            // THEN the verify button is disabled
            assertVerifyEmailSubmitIsDisabled()
        }
    }

    @Test
    fun `verify email button is enabled when 6 digit code is entered`() = testRule.runAccountFeatureTest {
        // GIVEN the verify email screen is shown
        goToVerifyEmail("user@example.com")
        awaitIdle()

        onVerifyEmail {
            // WHEN a 6-digit verification code is entered
            enterVerificationCode("123456")

            // THEN the verify button is enabled
            assertVerifyEmailSubmitIsEnabled()
        }
    }

    @Test
    fun `verify email shows loading state while request is in progress`() = testRule.runAccountFeatureTest {
        // GIVEN the verify email screen is shown with a 6-digit code entered
        goToVerifyEmail("user@example.com")
        awaitIdle()
        onVerifyEmail { enterVerificationCode("123456") }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueVerifyEmailResult(Result.success(Unit))

        // WHEN the verify button is tapped
        onVerifyEmail { tapVerifyEmailSubmit() }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onVerifyEmail {
            assertVerifyEmailLoadingIndicatorIsShown()
            assertVerifyEmailSubmitIsDisabled()
        }
    }

    @Test
    fun `verify email navigates to account screen on success`() = testRule.runAccountFeatureTest {
        // GIVEN the account screen is open and the verify email screen is navigated to
        goToAccount()
        awaitIdle()
        goToVerifyEmail("user@example.com")
        awaitIdle()
        onVerifyEmail { enterVerificationCode("123456") }
        enqueueVerifyEmailResult(Result.success(Unit))

        // WHEN the verify request succeeds
        onVerifyEmail { tapVerifyEmailSubmit() }
        awaitIdle()

        // THEN the account sign in screen is shown
        onAccount {
            assertSignInScreenIsShown()
        }
    }

    @Test
    fun `verify email shows error dialog on failure`() = testRule.runAccountFeatureTest {
        // GIVEN the verify email screen is shown with a 6-digit code entered
        goToVerifyEmail("user@example.com")
        awaitIdle()
        onVerifyEmail { enterVerificationCode("123456") }
        enqueueVerifyEmailResult(Result.failure<Unit>(Exception("Invalid code")))

        // WHEN the verify request fails
        onVerifyEmail { tapVerifyEmailSubmit() }
        awaitIdle()

        // THEN the error dialog is shown
        onVerifyEmail {
            errorDialog.assertIsShown()
        }
    }
}
