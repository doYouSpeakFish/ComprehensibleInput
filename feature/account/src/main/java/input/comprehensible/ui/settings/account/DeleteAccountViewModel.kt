package input.comprehensible.ui.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.account.usecases.DeleteAccountUseCase
import input.comprehensible.data.account.InvalidCredentialsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeleteAccountViewModel(
    private val deleteAccount: DeleteAccountUseCase = DeleteAccountUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState: StateFlow<DeleteAccountUiState> = _uiState.asStateFlow()

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onSubmit() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            deleteAccount(state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, accountDeleted = true) }
                }
                .onFailure { throwable ->
                    if (throwable is InvalidCredentialsException) {
                        _uiState.update { it.copy(isLoading = false, showInvalidCredentialsError = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, showError = true) }
                    }
                }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(showError = false) }
    }

    fun onInvalidCredentialsErrorDismissed() {
        _uiState.update { it.copy(showInvalidCredentialsError = false) }
    }

    fun onNavigationConsumed() {
        _uiState.update { it.copy(accountDeleted = false) }
    }
}
