package input.comprehensible.ui.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.account.usecases.RequestEmailVerificationCodeUseCase
import input.comprehensible.account.usecases.VerifyEmailUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val VERIFICATION_CODE_LENGTH = 6

class VerifyEmailViewModel(
    private val email: String,
    private val verifyEmail: VerifyEmailUseCase = VerifyEmailUseCase(),
    private val requestEmailVerificationCode: RequestEmailVerificationCodeUseCase = RequestEmailVerificationCodeUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerifyEmailUiState(email = email))
    val uiState: StateFlow<VerifyEmailUiState> = _uiState.asStateFlow()

    private var resendCooldownJob: Job? = null

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
                    startResendCooldown()
                }
                .onFailure {
                    _uiState.update { it.copy(isResendingCode = false, showError = true) }
                }
        }
    }

    /**
     * Disables the resend button for [RESEND_CODE_COOLDOWN_SECONDS] (matching the backend rate
     * limit) while counting the remaining seconds down for the "Resend in" label.
     */
    private fun startResendCooldown() {
        resendCooldownJob?.cancel()
        resendCooldownJob = viewModelScope.launchResendCodeCooldown { secondsRemaining ->
            _uiState.update { it.copy(resendCooldownSeconds = secondsRemaining) }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(showError = false) }
    }

    fun onNavigationConsumed() {
        _uiState.update { it.copy(verified = false) }
    }
}
