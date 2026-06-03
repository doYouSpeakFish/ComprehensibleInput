package input.comprehensible.ui.settings.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import input.comprehensible.account.usecases.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val PASSWORD_RESET_CODE_LENGTH = 6

class PasswordResetViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val resetPassword = ResetPasswordUseCase()
    private val email: String = savedStateHandle.toRoute<PasswordResetRoute>().email

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
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, showError = true) }
                }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(showError = false) }
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
