package input.comprehensible.features.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import input.comprehensible.ui.components.error.GENERIC_ERROR_DIALOG_TEST_TAG

class GenericErrorDialogRobot(private val composeTestRule: ComposeTestRule) {
    fun assertIsShown() {
        composeTestRule
            .onNodeWithTag(GENERIC_ERROR_DIALOG_TEST_TAG)
            .assertIsDisplayed()
    }

    fun dismiss() {
        composeTestRule
            .onNodeWithText("OK")
            .performClick()
    }
}

class AccountRobot(private val composeTestRule: ComposeTestRule) {
    val errorDialog = GenericErrorDialogRobot(composeTestRule)

    fun assertAccountTitleIsVisible() {
        composeTestRule
            .onNodeWithText("Account")
            .assertIsDisplayed()
    }

    // Sign in step

    fun enterSignInEmail(email: String) {
        composeTestRule
            .onNodeWithTag("account_sign_in_email_field")
            .performTextInput(email)
    }

    fun enterSignInPassword(password: String) {
        composeTestRule
            .onNodeWithTag("account_sign_in_password_field")
            .performTextInput(password)
    }

    fun assertSignInSubmitIsEnabled() {
        composeTestRule
            .onNodeWithTag("account_sign_in_submit_button")
            .assertIsEnabled()
    }

    fun assertSignInSubmitIsDisabled() {
        composeTestRule
            .onNodeWithTag("account_sign_in_submit_button")
            .assertIsNotEnabled()
    }

    fun tapSignIn() {
        composeTestRule
            .onNodeWithTag("account_sign_in_submit_button")
            .performClick()
    }

    fun assertSignInLoadingIndicatorIsShown() {
        composeTestRule
            .onNodeWithTag("account_sign_in_loading_indicator")
            .assertIsDisplayed()
    }

    fun assertSignUpFromSignInEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            composeTestRule.onNodeWithTag("account_sign_up_button").assertIsEnabled()
        } else {
            composeTestRule.onNodeWithTag("account_sign_up_button").assertIsNotEnabled()
        }
    }

    fun tapSignUpFromSignIn() {
        composeTestRule
            .onNodeWithTag("account_sign_up_button")
            .performClick()
    }

    fun assertInvalidCredentialsDialogIsShown() {
        composeTestRule
            .onNodeWithTag("account_invalid_credentials_dialog")
            .assertIsDisplayed()
    }

    fun dismissInvalidCredentialsDialog() {
        composeTestRule
            .onNodeWithText("OK")
            .performClick()
    }

    // Signed in step

    fun assertSignedInEmailIsShown(email: String) {
        composeTestRule
            .onNodeWithTag("account_signed_in_email")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(email)
            .assertIsDisplayed()
    }

    fun tapSignOut() {
        composeTestRule
            .onNodeWithTag("account_sign_out_button")
            .performClick()
    }

    fun assertSignInScreenIsShown() {
        composeTestRule
            .onNodeWithTag("account_sign_in_email_field")
            .assertIsDisplayed()
    }

    fun tapDeleteAccount() {
        composeTestRule
            .onNodeWithTag("account_delete_account_button")
            .performClick()
    }

    fun assertDeleteAccountButtonIsShown() {
        composeTestRule
            .onNodeWithTag("account_delete_account_button")
            .assertIsDisplayed()
    }
}

class SignUpRobot(private val composeTestRule: ComposeTestRule) {
    val errorDialog = GenericErrorDialogRobot(composeTestRule)

    fun enterEmail(email: String) {
        composeTestRule
            .onNodeWithTag("account_sign_up_email_field")
            .performTextInput(email)
    }

    fun enterPassword(password: String) {
        composeTestRule
            .onNodeWithTag("account_sign_up_password_field")
            .performTextInput(password)
    }

    fun enterConfirmPassword(confirmPassword: String) {
        composeTestRule
            .onNodeWithTag("account_sign_up_confirm_password_field")
            .performTextInput(confirmPassword)
    }

    fun assertSignUpSubmitIsEnabled() {
        composeTestRule
            .onNodeWithTag("account_sign_up_submit_button")
            .assertIsEnabled()
    }

    fun assertSignUpSubmitIsDisabled() {
        composeTestRule
            .onNodeWithTag("account_sign_up_submit_button")
            .assertIsNotEnabled()
    }

    fun tapSignUpSubmit() {
        composeTestRule
            .onNodeWithTag("account_sign_up_submit_button")
            .performClick()
    }

    fun assertSignUpLoadingIndicatorIsShown() {
        composeTestRule
            .onNodeWithTag("account_sign_up_loading_indicator")
            .assertIsDisplayed()
    }
}

class VerifyEmailRobot(private val composeTestRule: ComposeTestRule) {
    val errorDialog = GenericErrorDialogRobot(composeTestRule)

    fun assertEmailSentMessageIsShown(email: String) {
        composeTestRule
            .onNodeWithText(email, substring = true)
            .assertIsDisplayed()
    }

    fun enterVerificationCode(code: String) {
        composeTestRule
            .onNodeWithTag("account_verify_email_code_field")
            .performTextInput(code)
    }

    fun assertVerifyEmailSubmitIsEnabled() {
        composeTestRule
            .onNodeWithTag("account_verify_email_submit_button")
            .assertIsEnabled()
    }

    fun assertVerifyEmailSubmitIsDisabled() {
        composeTestRule
            .onNodeWithTag("account_verify_email_submit_button")
            .assertIsNotEnabled()
    }

    fun tapVerifyEmailSubmit() {
        composeTestRule
            .onNodeWithTag("account_verify_email_submit_button")
            .performClick()
    }

    fun assertVerifyEmailLoadingIndicatorIsShown() {
        composeTestRule
            .onNodeWithTag("account_verify_email_loading_indicator")
            .assertIsDisplayed()
    }
}

class DeleteAccountRobot(private val composeTestRule: ComposeTestRule) {
    val errorDialog = GenericErrorDialogRobot(composeTestRule)

    fun assertExplainerIsShown() {
        composeTestRule
            .onNodeWithTag("delete_account_explainer")
            .assertIsDisplayed()
    }

    fun assertWarningIsShown() {
        composeTestRule
            .onNodeWithTag("delete_account_warning")
            .assertIsDisplayed()
    }

    fun enterPassword(password: String) {
        composeTestRule
            .onNodeWithTag("delete_account_password_field")
            .performTextInput(password)
    }

    fun assertSubmitIsEnabled() {
        composeTestRule
            .onNodeWithTag("delete_account_submit_button")
            .assertIsEnabled()
    }

    fun assertSubmitIsDisabled() {
        composeTestRule
            .onNodeWithTag("delete_account_submit_button")
            .assertIsNotEnabled()
    }

    fun tapSubmit() {
        composeTestRule
            .onNodeWithTag("delete_account_submit_button")
            .performClick()
    }

    fun assertLoadingIndicatorIsShown() {
        composeTestRule
            .onNodeWithTag("delete_account_loading_indicator")
            .assertIsDisplayed()
    }

    fun assertInvalidCredentialsDialogIsShown() {
        composeTestRule
            .onNodeWithTag("delete_account_invalid_credentials_dialog")
            .assertIsDisplayed()
    }

    fun dismissInvalidCredentialsDialog() {
        composeTestRule
            .onNodeWithText("OK")
            .performClick()
    }
}
