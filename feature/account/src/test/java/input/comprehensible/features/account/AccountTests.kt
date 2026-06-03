package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.AccountFeatureTestScope
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.data.account.InvalidCredentialsException
import input.comprehensible.delayAccountRequests
import input.comprehensible.enqueueCreateAccountResult
import input.comprehensible.enqueueDeleteAccountResult
import input.comprehensible.enqueueSignInResult
import input.comprehensible.enqueueVerifyEmailResult
import input.comprehensible.onAccount
import input.comprehensible.onDeleteAccount
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
    fun `account sign in screenshot`() = testRule.runAccountFeatureTest {
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
    fun `sign in button is disabled when fields are empty`() = testRule.runAccountFeatureTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

        onAccount {
            // THEN the sign in button is disabled
            assertSignInSubmitIsDisabled()
        }
    }

    @Test
    fun `sign in button is disabled when only email is filled`() = testRule.runAccountFeatureTest {
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
    fun `sign in button is disabled when only password is filled`() = testRule.runAccountFeatureTest {
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
    fun `sign in button is enabled when email and password are filled`() = testRule.runAccountFeatureTest {
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
    fun `sign in shows loading state while request is in progress`() = testRule.runAccountFeatureTest {
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
            assertSignUpFromSignInEnabled(isEnabled = false)
        }
    }

    @Test
    fun `sign in navigates to signed in step on success`() = testRule.runAccountFeatureTest {
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
    fun `sign in error dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN a generic sign in error has occurred
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("password12345")
        }
        enqueueSignInResult(Result.failure<String>(Exception("Network error")))
        onAccount { tapSignIn() }
        awaitIdle()
        onAccount { errorDialog.assertIsShown() }

        // WHEN the error dialog is dismissed
        onAccount { errorDialog.dismiss() }
        awaitIdle()

        // THEN the sign in form is restored and the submit button is enabled
        onAccount {
            assertSignInSubmitIsEnabled()
        }
    }

    @Test
    fun `sign in invalid credentials dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN an invalid credentials error has occurred
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("wrongpassword12345")
        }
        enqueueSignInResult(Result.failure<String>(InvalidCredentialsException()))
        onAccount { tapSignIn() }
        awaitIdle()
        onAccount { invalidCredentialsDialog.assertIsShown() }

        // WHEN the invalid credentials dialog is dismissed
        onAccount { invalidCredentialsDialog.dismiss() }
        awaitIdle()

        // THEN the sign in form is restored and the submit button is enabled
        onAccount {
            assertSignInSubmitIsEnabled()
        }
    }

    @Test
    fun `sign in shows invalid credentials dialog on 401 error`() = testRule.runAccountFeatureTest {
        // GIVEN the account screen is open with fields filled
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("wrongpassword12345")
        }
        enqueueSignInResult(Result.failure<String>(InvalidCredentialsException()))

        // WHEN the sign in request fails with invalid credentials
        onAccount { tapSignIn() }
        awaitIdle()

        // THEN the invalid credentials dialog is shown
        onAccount {
            invalidCredentialsDialog.assertIsShown()
        }
    }

    @Test
    fun `sign in shows generic error dialog on non-credentials failure`() = testRule.runAccountFeatureTest {
        // GIVEN the account screen is open with fields filled
        goToAccount()
        awaitIdle()
        onAccount {
            enterSignInEmail("user@example.com")
            enterSignInPassword("password12345")
        }
        enqueueSignInResult(Result.failure<String>(Exception("Network error")))

        // WHEN the sign in request fails with a generic error
        onAccount { tapSignIn() }
        awaitIdle()

        // THEN the generic error dialog is shown
        onAccount {
            errorDialog.assertIsShown()
        }
    }

    @Test
    fun `sign up button navigates to sign up screen`() = testRule.runAccountFeatureTest {
        // GIVEN the account screen is open showing the sign in step
        goToAccount()
        awaitIdle()

        // WHEN the sign up button is tapped
        onAccount { tapSignUpFromSignIn() }
        awaitIdle()

        // THEN the sign up form is shown
        onSignUp {
            assertSignUpSubmitIsDisabled()
        }
    }

    // Signed in tests

    @Test
    fun `account shows signed in state when session exists`() = testRule.runAccountFeatureTest {
        // GIVEN a session has been saved
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")

        // WHEN the account screen is opened
        goToAccount()
        awaitIdle()

        // THEN the signed in state is shown with the user's email
        onAccount {
            assertSignedInEmailIsShown("user@example.com")
        }
    }

    @Test
    fun `sign out returns to sign in state`() = testRule.runAccountFeatureTest {
        // GIVEN the user is signed in
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
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

    // Delete account tests

    @Test
    fun `delete account button is shown when signed in`() = testRule.runAccountFeatureTest {
        // GIVEN the user is signed in
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToAccount()
        awaitIdle()

        // THEN the delete account button is shown
        onAccount {
            assertDeleteAccountButtonIsShown()
        }
    }

    @Test
    fun `delete account button navigates to delete account screen`() = testRule.runAccountFeatureTest {
        // GIVEN the user is signed in and on the account screen
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToAccount()
        awaitIdle()

        // WHEN the delete account button is tapped
        onAccount { tapDeleteAccount() }
        awaitIdle()

        // THEN the delete account screen is shown with the explainer and warning
        onDeleteAccount {
            assertExplainerIsShown()
            assertWarningIsShown()
        }
    }

    @Test
    fun `delete account submit is disabled when password is empty`() = testRule.runAccountFeatureTest {
        // GIVEN the delete account screen is shown
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()

        // THEN the submit button is disabled
        onDeleteAccount {
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `delete account submit is enabled when password is filled`() = testRule.runAccountFeatureTest {
        // GIVEN the delete account screen is shown
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()

        onDeleteAccount {
            // WHEN the password is entered
            enterPassword("password12345")

            // THEN the submit button is enabled
            assertSubmitIsEnabled()
        }
    }

    @Test
    fun `delete account shows loading state while request is in progress`() = testRule.runAccountFeatureTest {
        // GIVEN the delete account screen is shown with a password entered
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()
        onDeleteAccount { enterPassword("password12345") }
        // The request is kept in-flight so the loading state can be observed before it completes
        delayAccountRequests(delayMillis = 1_000L)
        enqueueDeleteAccountResult(Result.success(Unit))

        // WHEN the delete button is tapped
        onDeleteAccount { tapSubmit() }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onDeleteAccount {
            assertLoadingIndicatorIsShown()
            assertSubmitIsDisabled()
        }
    }

    @Test
    fun `delete account navigates to sign in screen on success`() = testRule.runAccountFeatureTest {
        // GIVEN the user is signed in and on the delete account screen
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToAccount()
        awaitIdle()
        goToDeleteAccount()
        awaitIdle()
        onDeleteAccount { enterPassword("password12345") }
        enqueueDeleteAccountResult(Result.success(Unit))

        // WHEN the delete account request succeeds
        onDeleteAccount { tapSubmit() }
        awaitIdle()

        // THEN the sign in screen is shown (session has been cleared)
        onAccount {
            assertSignInScreenIsShown()
        }
    }

    @Test
    fun `delete account shows invalid credentials error on wrong password`() = testRule.runAccountFeatureTest {
        // GIVEN the delete account screen is shown with a password entered
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()
        onDeleteAccount { enterPassword("wrongpassword") }
        enqueueDeleteAccountResult(Result.failure<Unit>(InvalidCredentialsException()))

        // WHEN the delete request fails with invalid credentials
        onDeleteAccount { tapSubmit() }
        awaitIdle()

        // THEN the invalid credentials dialog is shown
        onDeleteAccount {
            assertInvalidCredentialsDialogIsShown()
        }
    }

    @Test
    fun `delete account shows generic error on other failure`() = testRule.runAccountFeatureTest {
        // GIVEN the delete account screen is shown with a password entered
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()
        onDeleteAccount { enterPassword("password12345") }
        enqueueDeleteAccountResult(Result.failure<Unit>(Exception("Network error")))

        // WHEN the delete request fails with a generic error
        onDeleteAccount { tapSubmit() }
        awaitIdle()

        // THEN the generic error dialog is shown
        onDeleteAccount {
            errorDialog.assertIsShown()
        }
    }

    @Test
    fun `delete account generic error dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN a generic delete account error has occurred
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()
        onDeleteAccount { enterPassword("password12345") }
        enqueueDeleteAccountResult(Result.failure<Unit>(Exception("Network error")))
        onDeleteAccount { tapSubmit() }
        awaitIdle()
        onDeleteAccount { errorDialog.assertIsShown() }

        // WHEN the error dialog is dismissed
        onDeleteAccount { errorDialog.dismiss() }
        awaitIdle()

        // THEN the submit button is re-enabled
        onDeleteAccount {
            assertSubmitIsEnabled()
        }
    }

    @Test
    fun `delete account invalid credentials dialog can be dismissed`() = testRule.runAccountFeatureTest {
        // GIVEN an invalid credentials error has occurred
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()
        onDeleteAccount { enterPassword("wrongpassword") }
        enqueueDeleteAccountResult(Result.failure<Unit>(InvalidCredentialsException()))
        onDeleteAccount { tapSubmit() }
        awaitIdle()
        onDeleteAccount { assertInvalidCredentialsDialogIsShown() }

        // WHEN the invalid credentials dialog is dismissed
        onDeleteAccount { dismissInvalidCredentialsDialog() }
        awaitIdle()

        // THEN the submit button is re-enabled
        onDeleteAccount {
            assertSubmitIsEnabled()
        }
    }

    @Test
    fun `delete account does not clear session on failure`() = testRule.runAccountFeatureTest {
        // GIVEN the user is signed in and the delete account request fails
        realAccountLocalDataSource.saveSession(token = "test-token", email = "user@example.com")
        goToDeleteAccount()
        awaitIdle()
        onDeleteAccount { enterPassword("wrongpassword") }
        enqueueDeleteAccountResult(Result.failure<Unit>(InvalidCredentialsException()))
        onDeleteAccount { tapSubmit() }
        awaitIdle()

        // THEN navigating to the account screen still shows the signed-in state
        goToAccount()
        awaitIdle()
        onAccount {
            assertSignedInEmailIsShown("user@example.com")
        }
    }
}
