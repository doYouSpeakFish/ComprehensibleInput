package input.comprehensible.ui.settings.account

data class VerifyEmailUiState(
    val email: String,
    val code: String = "",
    val isLoading: Boolean = false,
    val isResendingCode: Boolean = false,
    val codeResent: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val showError: Boolean = false,
    val verified: Boolean = false,
)
