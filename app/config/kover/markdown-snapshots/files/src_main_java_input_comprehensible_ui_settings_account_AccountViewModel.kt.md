# src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 6-10

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:6-10`

```kotlin
⚪    6 | import kotlinx.coroutines.flow.asStateFlow
⚪    7 | 
🔴    8 | class AccountViewModel : ViewModel() {
🔴    9 |     val uiState: StateFlow<AccountUiState> = MutableStateFlow(AccountUiState.INITIAL).asStateFlow()
⚪   10 | }
```
