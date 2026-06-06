package input.comprehensible.ui.settings.account

data class PasswordResetUiState(
    val email: String,
    val code: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isResendingCode: Boolean = false,
    val codeResent: Boolean = false,
    val showError: Boolean = false,
    val showInvalidCodeError: Boolean = false,
    val passwordReset: Boolean = false,
)
