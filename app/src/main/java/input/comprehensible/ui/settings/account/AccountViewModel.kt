package input.comprehensible.ui.settings.account

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountViewModel : ViewModel() {
    val uiState: StateFlow<AccountUiState> = MutableStateFlow(AccountUiState.INITIAL).asStateFlow()
}
