package input.comprehensible.ui.settings.account

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val accountCreatedForEmail: String? = null,
)
