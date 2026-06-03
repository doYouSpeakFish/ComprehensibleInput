package input.comprehensible.ui.settings.account

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.account.usecases.RequestPasswordResetCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val requestPasswordResetCode: RequestPasswordResetCodeUseCase = RequestPasswordResetCodeUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onSubmit() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            requestPasswordResetCode(state.email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, codeSentToEmail = state.email) }
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
        _uiState.update { it.copy(codeSentToEmail = null) }
    }
}

internal fun ForgotPasswordUiState.isSubmitEnabled(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}
