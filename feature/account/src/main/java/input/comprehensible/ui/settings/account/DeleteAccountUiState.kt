package input.comprehensible.ui.settings.account

data class DeleteAccountUiState(
    val password: String = "",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val showInvalidCredentialsError: Boolean = false,
    val accountDeleted: Boolean = false,
)

internal fun DeleteAccountUiState.isSubmitEnabled(): Boolean = password.isNotBlank()
