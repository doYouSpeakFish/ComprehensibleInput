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

class SignUpViewModel(
    private val accountRepository: AccountRepository = AccountRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
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
            accountRepository.createAccount(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, accountCreatedForEmail = state.email) }
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
        _uiState.update { it.copy(accountCreatedForEmail = null) }
    }
}

internal fun SignUpUiState.isSubmitEnabled(): Boolean {
    val trimmedEmail = email.trim()
    return trimmedEmail.isNotBlank() &&
        trimmedEmail.contains('@') &&
        password.length >= MINIMUM_PASSWORD_LENGTH &&
        password == confirmPassword
}
