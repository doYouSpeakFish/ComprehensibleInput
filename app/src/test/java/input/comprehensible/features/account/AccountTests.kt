package input.comprehensible.features.account

import android.app.Application
import android.os.Build
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import input.comprehensible.ComprehensibleInputTestRule
import input.comprehensible.ThemeMode
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

    @Test
    fun `account sign up screenshot`() = testRule.runTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

        // THEN the sign up form is shown with the account title
        onAccount {
            assertAccountTitleIsVisible()
        }
    }

    @Test
    fun `sign up button is disabled when fields are empty`() = testRule.runTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

        onAccount {
            // THEN the submit button is disabled
            assertSignUpSubmitIsDisabled()
        }
    }

    @Test
    fun `sign up button is disabled when email is missing`() = testRule.runTest {
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

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
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

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
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

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
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

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
        // GIVEN the account screen is open
        goToAccount()
        awaitIdle()

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
        // GIVEN the account screen is open with valid fields filled
        goToAccount()
        awaitIdle()
        onAccount {
            enterEmail("user@example.com")
            enterPassword("password12345")
            enterConfirmPassword("password12345")
        }
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
        // GIVEN the account screen is open with valid fields filled
        goToAccount()
        awaitIdle()
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
        // GIVEN the account screen is open with valid fields filled
        goToAccount()
        awaitIdle()
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

    @Test
    fun `verify email button is disabled when code is empty`() = testRule.runTest {
        // GIVEN the verify email step is shown
        goToAccount()
        awaitIdle()
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

        // WHEN the verify button is tapped
        onAccount { tapVerifyEmailSubmit() }

        // THEN the loading indicator is shown and the button is disabled before the request completes
        onAccount {
            assertVerifyEmailLoadingIndicatorIsShown()
            assertVerifyEmailSubmitIsDisabled()
        }
    }

    @Test
    fun `verify email shows error dialog on failure`() = testRule.runTest {
        // GIVEN the verify email step is shown with a 6-digit code entered
        goToAccount()
        awaitIdle()
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
