package input.comprehensible.ui.settings.account

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val codeSentToEmail: String? = null,
)
