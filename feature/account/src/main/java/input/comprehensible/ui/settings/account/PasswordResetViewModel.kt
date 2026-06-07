package input.comprehensible.ui.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.account.usecases.RequestPasswordResetCodeUseCase
import input.comprehensible.account.usecases.ResetPasswordUseCase
import input.comprehensible.data.account.InvalidResetCodeException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val PASSWORD_RESET_CODE_LENGTH = 6
private const val MINIMUM_PASSWORD_LENGTH = 12

class PasswordResetViewModel(
    private val email: String,
    private val resetPassword: ResetPasswordUseCase = ResetPasswordUseCase(),
    private val requestPasswordResetCode: RequestPasswordResetCodeUseCase = RequestPasswordResetCodeUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordResetUiState(email = email))
    val uiState: StateFlow<PasswordResetUiState> = _uiState.asStateFlow()

    fun onCodeChanged(code: String) {
        _uiState.update { it.copy(code = code) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onSubmit() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            resetPassword(state.email, state.password, state.code)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, passwordReset = true) }
                }
                .onFailure { throwable ->
                    if (throwable is InvalidResetCodeException) {
                        _uiState.update { it.copy(isLoading = false, showInvalidCodeError = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, showError = true) }
                    }
                }
        }
    }

    fun onResendCode() {
        val state = _uiState.value
        _uiState.update { it.copy(isResendingCode = true, codeResent = false) }
        viewModelScope.launch {
            requestPasswordResetCode(state.email)
                .onSuccess {
                    // Requesting a new code invalidates any code the user already entered, so clear
                    // it to require the freshly delivered one.
                    _uiState.update {
                        it.copy(isResendingCode = false, codeResent = true, code = "")
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isResendingCode = false, showError = true) }
                }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(showError = false) }
    }

    fun onInvalidCodeErrorDismissed() {
        _uiState.update { it.copy(showInvalidCodeError = false) }
    }

    fun onNavigationConsumed() {
        _uiState.update { it.copy(passwordReset = false) }
    }
}

internal fun PasswordResetUiState.isSubmitEnabled(): Boolean {
    return code.length == PASSWORD_RESET_CODE_LENGTH &&
        password.length >= MINIMUM_PASSWORD_LENGTH &&
        password == confirmPassword
}
