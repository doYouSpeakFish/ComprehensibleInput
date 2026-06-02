package input.comprehensible.ui.settings.account

data class AccountUiState(
    val step: Step,
    val showError: Boolean = false,
    val showInvalidCredentialsError: Boolean = false,
) {
    sealed interface Step {
        data object Loading : Step

        data class SignedIn(val email: String) : Step

        data class SignIn(
            val email: String = "",
            val password: String = "",
            val isLoading: Boolean = false,
        ) : Step

        data class SignUp(
            val email: String = "",
            val password: String = "",
            val confirmPassword: String = "",
            val isLoading: Boolean = false,
        ) : Step

        data class VerifyEmail(
            val email: String,
            val code: String = "",
            val isLoading: Boolean = false,
        ) : Step
    }

    companion object {
        val INITIAL = AccountUiState(step = Step.Loading)
    }
}
