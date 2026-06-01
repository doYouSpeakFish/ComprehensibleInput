# src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 21-25

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:21-25`

```kotlin
⚪   21 |     fun onEmailChanged(email: String) {
🟢   22 |         _uiState.update { state ->
🟡   23 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢   24 |             state.copy(step = step.copy(email = email))
⚪   25 |         }
```

## Lines 28-32

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:28-32`

```kotlin
⚪   28 |     fun onPasswordChanged(password: String) {
🟢   29 |         _uiState.update { state ->
🟡   30 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢   31 |             state.copy(step = step.copy(password = password))
⚪   32 |         }
```

## Lines 35-39

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:35-39`

```kotlin
⚪   35 |     fun onConfirmPasswordChanged(confirmPassword: String) {
🟢   36 |         _uiState.update { state ->
🟡   37 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢   38 |             state.copy(step = step.copy(confirmPassword = confirmPassword))
⚪   39 |         }
```

## Lines 41-45

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:41-45`

```kotlin
⚪   41 | 
⚪   42 |     fun onSignUpSubmit() {
🟡   43 |         val step = _uiState.value.step as? AccountUiState.Step.SignUp ?: return
🟢   44 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢   45 |         viewModelScope.launch {
```

## Lines 58-62

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:58-62`

```kotlin
⚪   58 |     fun onCodeChanged(code: String) {
🟢   59 |         _uiState.update { state ->
🟡   60 |             val step = state.step as? AccountUiState.Step.VerifyEmail ?: return
🟢   61 |             state.copy(step = step.copy(code = code))
⚪   62 |         }
```

## Lines 64-68

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:64-68`

```kotlin
⚪   64 | 
⚪   65 |     fun onVerifyEmailSubmit() {
🟡   66 |         val step = _uiState.value.step as? AccountUiState.Step.VerifyEmail ?: return
🟢   67 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢   68 |         viewModelScope.launch {
```
