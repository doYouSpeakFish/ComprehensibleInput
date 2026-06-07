package input.comprehensible.ui.settings.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import input.comprehensible.account.usecases.RequestEmailVerificationCodeUseCase
import input.comprehensible.account.usecases.VerifyEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val VERIFICATION_CODE_LENGTH = 6

class VerifyEmailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val verifyEmail = VerifyEmailUseCase()
    private val requestEmailVerificationCode = RequestEmailVerificationCodeUseCase()
    private val email: String = savedStateHandle.toRoute<VerifyEmailRoute>().email

    private val _uiState = MutableStateFlow(VerifyEmailUiState(email = email))
    val uiState: StateFlow<VerifyEmailUiState> = _uiState.asStateFlow()

    fun onCodeChanged(code: String) {
        _uiState.update { it.copy(code = code) }
    }

    fun onSubmit() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            verifyEmail(state.email, state.code)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, verified = true) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, showError = true) }
                }
        }
    }

    fun onResendCode() {
        val state = _uiState.value
        _uiState.update { it.copy(isResendingCode = true, codeResent = false) }
        viewModelScope.launch {
            requestEmailVerificationCode(state.email)
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

    fun onNavigationConsumed() {
        _uiState.update { it.copy(verified = false) }
    }
}
