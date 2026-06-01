package input.comprehensible.ui.settings.account

data class AccountUiState(
    val step: Step,
    val showError: Boolean = false,
) {
    sealed interface Step {
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

        data object Verified : Step
    }

    companion object {
        val INITIAL = AccountUiState(step = Step.SignUp())
    }
}
