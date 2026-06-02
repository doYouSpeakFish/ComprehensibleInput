package input.comprehensible.ui.settings.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import input.comprehensible.data.account.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val VERIFICATION_CODE_LENGTH = 6

class VerifyEmailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val accountRepository = AccountRepository()
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
            accountRepository.verifyEmail(state.email, state.code)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, verified = true) }
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
        _uiState.update { it.copy(verified = false) }
    }
}
