package input.comprehensible.ui.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.data.account.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val MINIMUM_PASSWORD_LENGTH = 12
internal const val VERIFICATION_CODE_LENGTH = 6

class AccountViewModel(
    private val accountRepository: AccountRepository = AccountRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState.INITIAL)
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { state ->
            val step = state.step as? AccountUiState.Step.SignUp ?: return
            state.copy(step = step.copy(email = email))
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { state ->
            val step = state.step as? AccountUiState.Step.SignUp ?: return
            state.copy(step = step.copy(password = password))
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { state ->
            val step = state.step as? AccountUiState.Step.SignUp ?: return
            state.copy(step = step.copy(confirmPassword = confirmPassword))
        }
    }

    fun onSignUpSubmit() {
        val step = _uiState.value.step as? AccountUiState.Step.SignUp ?: return
        _uiState.update { it.copy(step = step.copy(isLoading = true)) }
        viewModelScope.launch {
            accountRepository.createAccount(step.email, step.password)
                .onSuccess {
                    _uiState.update {
                        AccountUiState(step = AccountUiState.Step.VerifyEmail(email = step.email))
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(step = step.copy(isLoading = false), showError = true) }
                }
        }
    }

    fun onCodeChanged(code: String) {
        _uiState.update { state ->
            val step = state.step as? AccountUiState.Step.VerifyEmail ?: return
            state.copy(step = step.copy(code = code))
        }
    }

    fun onVerifyEmailSubmit() {
        val step = _uiState.value.step as? AccountUiState.Step.VerifyEmail ?: return
        _uiState.update { it.copy(step = step.copy(isLoading = true)) }
        viewModelScope.launch {
            accountRepository.verifyEmail(step.email, step.code)
                .onSuccess {
                    // Email verified successfully
                }
                .onFailure {
                    _uiState.update { it.copy(step = step.copy(isLoading = false), showError = true) }
                }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(showError = false) }
    }
}

internal fun AccountUiState.Step.SignUp.isSubmitEnabled(): Boolean {
    val trimmedEmail = email.trim()
    return trimmedEmail.isNotBlank() &&
        trimmedEmail.contains('@') &&
        password.length >= MINIMUM_PASSWORD_LENGTH &&
        password == confirmPassword
}
