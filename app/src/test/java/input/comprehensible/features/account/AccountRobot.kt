package input.comprehensible.features.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import input.comprehensible.ComprehensibleInputTestScope
import input.comprehensible.ui.components.error.GENERIC_ERROR_DIALOG_TEST_TAG

class AccountRobot(private val composeTestRule: ComposeTestRule) {
    fun assertAccountTitleIsVisible() {
        composeTestRule
            .onNodeWithText("Account")
            .assertIsDisplayed()
    }

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

    fun assertErrorDialogIsShown() {
        composeTestRule
            .onNodeWithTag(GENERIC_ERROR_DIALOG_TEST_TAG)
            .assertIsDisplayed()
    }

    fun dismissErrorDialog() {
        composeTestRule
            .onNodeWithText("OK")
            .performClick()
    }
}

suspend fun ComprehensibleInputTestScope.onAccount(
    block: suspend AccountRobot.() -> Unit = {},
) = AccountRobot(composeRule).apply { block() }
