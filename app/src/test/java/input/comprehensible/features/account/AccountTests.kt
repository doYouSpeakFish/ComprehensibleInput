package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.data.account.InvalidCredentialsException
import input.comprehensible.runTest
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
class AccountTests(private val themeMode: ThemeMode) {
    @get:Rule
    val testRule = ComprehensibleInputTestRule(themeMode)

    companion object {
        @Suppress("unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "theme = {0}")
        fun parameters() = ThemeMode.entries.map { arrayOf(it) }
    }

    // Sign in tests

    @Test
    fun `account sign in screenshot`() = testRule.runTest {
        // GIVEN the account screen is open with no session
        goToAccount()
        awaitIdle()

        // THEN the sign in form is shown
        onAccount {
            assertAccountTitleIsVisible()
            assertSignInScreenIsShown()
        }
    }

    @Test
    fun `sign in button is disabled when fields are empty`() = testRule.runTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

        onAccount {
            // THEN the sign in button is disabled
            assertSignInSubmitIsDisabled()
        }
    }

    @Test
    fun `sign in button is disabled when only email is filled`() = testRule.runTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

        onAccount {
            // WHEN only the email is filled
            enterSignInEmail("user@example.com")

            // THEN the sign in button is disabled
            assertSignInSubmitIsDisabled()
        }
    }

    @Test
    fun `sign in button is disabled when only password is filled`() = testRule.runTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

        onAccount {
            // WHEN only the password is filled
            enterSignInPassword("password12345")

            // THEN the sign in button is disabled
            assertSignInSubmitIsDisabled()
        }
    }

    @Test
    fun `sign in button is enabled when email and password are filled`() = testRule.runTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

        onAccount {
            // WHEN both fields are filled
            enterSignInEmail("user@example.com")
            enterSignInPassword("password12345")

            // THEN the sign in button is enabled
            assertSignInSubmitIsEnabled()
        }
    }

    @Test
    fun `sign in shows loading state while request is in progress`() = testRule.runTest {
        // GIVEN the account screen is open with valid fields filled
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("password12345")
        }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueSignInResult(Result.success("token123"))

        // WHEN the sign in button is tapped
        onAccount { tapSignIn() }

        // THEN the loading indicator is shown, sign in button is disabled, and sign up button is disabled
        onAccount {
            assertSignInLoadingIndicatorIsShown()
            assertSignInSubmitIsDisabled()
            assertSignUpFromSignInIsDisabled()
        }
    }

    @Test
    fun `sign in navigates to signed in step on success`() = testRule.runTest {
        // GIVEN the account screen is open with valid fields filled
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("password12345")
        }
        enqueueSignInResult(Result.success("token123"))

        // WHEN the sign in request succeeds
        onAccount { tapSignIn() }
        awaitIdle()

        // THEN the signed in state is shown with the user's email
        onAccount {
            assertSignedInEmailIsShown("user@example.com")
        }
    }

    @Test
    fun `sign in shows invalid credentials dialog on 401 error`() = testRule.runTest {
        // GIVEN the account screen is open with fields filled
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("wrongpassword12345")
        }
        enqueueSignInResult(Result.failure(InvalidCredentialsException()))

        // WHEN the sign in request fails with invalid credentials
        onAccount { tapSignIn() }
        awaitIdle()

        // THEN the invalid credentials dialog is shown
        onAccount {
            assertInvalidCredentialsDialogIsShown()
        }
    }

    @Test
    fun `sign in shows generic error dialog on non-credentials failure`() = testRule.runTest {
        // GIVEN the account screen is open with fields filled
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("password12345")
        }
        enqueueSignInResult(Result.failure(Exception("Network error")))

        // WHEN the sign in request fails with a generic error
        onAccount { tapSignIn() }
        awaitIdle()

        // THEN the generic error dialog is shown
        onAccount {
            assertErrorDialogIsShown()
        }
    }

    @Test
    fun `sign up button navigates to sign up step`() = testRule.runTest {
        // GIVEN the account screen is open showing the sign in step
        goToAccount()
        awaitIdle()

        // WHEN the sign up button is tapped
        onAccount { tapSignUpFromSignIn() }
        awaitIdle()

        // THEN the sign up form is shown
        onAccount {
            assertSignUpSubmitIsDisabled()
        }
    }

    // Signed in tests

    @Test
    fun `account shows signed in state when session exists`() = testRule.runTest {
        // GIVEN a session has been saved
        saveAccountSession(token = "test-token", email = "user@example.com")

        // WHEN the account screen is opened
        goToAccount()
        awaitIdle()

        // THEN the signed in state is shown with the user's email
        onAccount {
            assertSignedInEmailIsShown("user@example.com")
        }
    }

    @Test
    fun `sign out returns to sign in state`() = testRule.runTest {
        // GIVEN the user is signed in
        saveAccountSession(token = "test-token", email = "user@example.com")
        goToAccount()
        awaitIdle()

        // WHEN the sign out button is tapped
        onAccount { tapSignOut() }
        awaitIdle()

        // THEN the sign in state is shown
        onAccount {
            assertSignInScreenIsShown()
        }
    }

    // Sign up tests

    @Test
    fun `sign up button is disabled when fields are empty`() = testRule.runTest {
        // GIVEN the sign up step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }

        onAccount {
            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when email is missing`() = testRule.runTest {
        // GIVEN the sign up step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }

        onAccount {
            // WHEN password and confirm password are filled but email is not entered
            enterPassword("password12345")
            enterConfirmPassword("password12345")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when email is invalid`() = testRule.runTest {
        // GIVEN the sign up step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }

        onAccount {
            // WHEN an email without an @ symbol is entered
            enterEmail("invalidemail")
            enterPassword("password12345")
            enterConfirmPassword("password12345")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when password is too short`() = testRule.runTest {
        // GIVEN the sign up step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }

        onAccount {
            // WHEN a password shorter than 12 characters is entered
            enterEmail("user@example.com")
            enterPassword("short")
            enterConfirmPassword("short")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when passwords do not match`() = testRule.runTest {
        // GIVEN the sign up step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }

        onAccount {
            // WHEN a valid email and password are entered but the confirm password does not match
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("differentpassword12345")

            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is enabled when all fields are valid`() = testRule.runTest {
        // GIVEN the sign up step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }

        onAccount {
            // WHEN all fields are filled with valid values and passwords match
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")

            // THEN the submit button is enabled
            assertSignUpSubmitIsEnabled()
        }
    }

    @Test
    fun `sign up shows loading state while request is in progress`() = testRule.runTest {
        // GIVEN the sign up step is shown with valid fields filled
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueCreateAccountResult(Result.success(Unit))

        // WHEN the sign up button is tapped
        onAccount {
            tapSignUpSubmit()
        }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onAccount {
            assertSignUpLoadingIndicatorIsShown()
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up navigates to verify email step on success`() = testRule.runTest {
        // GIVEN the sign up step is shown with valid fields filled
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))

        // WHEN the sign up request succeeds
        onAccount {
            tapSignUpSubmit()
        }
        awaitIdle()

        // THEN the email verification step is shown with the submitted email address
        onAccount {
            assertEmailSentMessageIsShown("user@example.com")
        }
    }

    @Test
    fun `sign up shows error dialog on failure`() = testRule.runTest {
        // GIVEN the sign up step is shown with valid fields filled
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.failure(Exception("Network error")))

        // WHEN the sign up request fails
        onAccount {
            tapSignUpSubmit()
        }
        awaitIdle()

        // THEN the error dialog is shown
        onAccount {
            assertErrorDialogIsShown()
        }
    }

    @Test
    fun `sign up error dialog can be dismissed`() = testRule.runTest {
        // GIVEN a sign up error has occurred
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.failure(Exception("Network error")))
        onAccount { tapSignUpSubmit() }
        awaitIdle()
        onAccount { assertErrorDialogIsShown() }

        // WHEN the error dialog is dismissed
        onAccount { dismissErrorDialog() }
        awaitIdle()

        // THEN the sign up form is restored and the submit button is enabled
        onAccount {
            assertSignUpSubmitIsEnabled()
        }
    }

    // Verify email tests

    @Test
    fun `verify email button is disabled when code is empty`() = testRule.runTest {
        // GIVEN the verify email step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))
        onAccount { tapSignUpSubmit() }
        awaitIdle()

        onAccount {
            // THEN the verify button is disabled when no code has been entered
            assertVerifyEmailSubmitIsDisabled()
        }
    }

    @Test
    fun `verify email button is disabled when code is less than 6 digits`() = testRule.runTest {
        // GIVEN the verify email step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))
        onAccount { tapSignUpSubmit() }
        awaitIdle()

        onAccount {
            // WHEN a code with fewer than 6 digits is entered
            enterVerificationCode("123")

            // THEN the verify button is disabled
            assertVerifyEmailSubmitIsDisabled()
        }
    }

    @Test
    fun `verify email button is enabled when 6 digit code is entered`() = testRule.runTest {
        // GIVEN the verify email step is shown
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))
        onAccount { tapSignUpSubmit() }
        awaitIdle()

        onAccount {
            // WHEN a 6-digit verification code is entered
            enterVerificationCode("123456")

            // THEN the verify button is enabled
            assertVerifyEmailSubmitIsEnabled()
        }
    }

    @Test
    fun `verify email shows loading state while request is in progress`() = testRule.runTest {
        // GIVEN the verify email step is shown with a 6-digit code entered
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))
        onAccount { tapSignUpSubmit() }
        awaitIdle()
        onAccount { enterVerificationCode("123456") }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueVerifyEmailResult(Result.success(Unit))

        // WHEN the verify button is tapped
        onAccount { tapVerifyEmailSubmit() }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onAccount {
            assertVerifyEmailLoadingIndicatorIsShown()
            assertVerifyEmailSubmitIsDisabled()
        }
    }

    @Test
    fun `verify email navigates to sign in step on success`() = testRule.runTest {
        // GIVEN the verify email step is shown with a 6-digit code entered
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))
        onAccount { tapSignUpSubmit() }
        awaitIdle()
        onAccount { enterVerificationCode("123456") }
        enqueueVerifyEmailResult(Result.success(Unit))

        // WHEN the verify request succeeds
        onAccount { tapVerifyEmailSubmit() }
        awaitIdle()

        // THEN the sign in step is shown
        onAccount {
            assertSignInScreenIsShown()
        }
    }

    @Test
    fun `verify email shows error dialog on failure`() = testRule.runTest {
        // GIVEN the verify email step is shown with a 6-digit code entered
        goToAccount()
        awaitIdle()
        onAccount { tapSignUpFromSignIn() }
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
        enqueueCreateAccountResult(Result.success(Unit))
        onAccount { tapSignUpSubmit() }
        awaitIdle()
        onAccount { enterVerificationCode("123456") }
        enqueueVerifyEmailResult(Result.failure(Exception("Invalid code")))

        // WHEN the verify request fails
        onAccount { tapVerifyEmailSubmit() }
        awaitIdle()

        // THEN the error dialog is shown
        onAccount {
            assertErrorDialogIsShown()
        }
    }
}
