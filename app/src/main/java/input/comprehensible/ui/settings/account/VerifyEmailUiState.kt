package input.comprehensible.ui.settings.account

data class VerifyEmailUiState(
    val email: String,
    val code: String = "",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val verified: Boolean = false,
)
