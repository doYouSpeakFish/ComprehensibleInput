package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.AccountFeatureTestScope
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
import input.comprehensible.data.account.InvalidCredentialsException
import input.comprehensible.delayAccountRequests
import input.comprehensible.enqueueDeleteAccountResult
import input.comprehensible.enqueueSignInResult
import input.comprehensible.onAccount
import input.comprehensible.onDeleteAccount
import input.comprehensible.onSignUp
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
            assertSignInSubmitEnabled(isEnabled = false)
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
            assertSignInSubmitEnabled(isEnabled = false)
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
            assertSignInSubmitEnabled(isEnabled = false)
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
            assertSignInSubmitEnabled(isEnabled = true)
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
            assertSignInSubmitEnabled(isEnabled = false)
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
            assertSignInSubmitEnabled(isEnabled = true)
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
        onAccount { assertInvalidCredentialsDialogIsShown() }

        // WHEN the invalid credentials dialog is dismissed
        onAccount { dismissInvalidCredentialsDialog() }
        awaitIdle()

        // THEN the sign in form is restored and the submit button is enabled
        onAccount {
            assertSignInSubmitEnabled(isEnabled = true)
        }
    }

    @Test
    fun `sign in shows invalid credentials dialog on invalid credentials`() = testRule.runAccountFeatureTest {
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
            assertInvalidCredentialsDialogIsShown()
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
