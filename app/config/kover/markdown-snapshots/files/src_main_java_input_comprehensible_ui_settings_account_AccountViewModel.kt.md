# src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 24-28

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:24-28`

```kotlin
🟢   24 |             val token = accountRepository.getSessionToken()
🟢   25 |             val email = accountRepository.getEmail()
🟡   26 |             if (token != null && email != null) {
🟢   27 |                 _uiState.update { AccountUiState(step = AccountUiState.Step.SignedIn(email)) }
⚪   28 |             } else {
```

## Lines 34-38

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:34-38`

```kotlin
⚪   34 |     fun onSignInEmailChanged(email: String) {
🟢   35 |         _uiState.update { state ->
🟡   36 |             val step = state.step as? AccountUiState.Step.SignIn ?: return
🟢   37 |             state.copy(step = step.copy(email = email))
⚪   38 |         }
```

## Lines 41-45

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:41-45`

```kotlin
⚪   41 |     fun onSignInPasswordChanged(password: String) {
🟢   42 |         _uiState.update { state ->
🟡   43 |             val step = state.step as? AccountUiState.Step.SignIn ?: return
🟢   44 |             state.copy(step = step.copy(password = password))
⚪   45 |         }
```

## Lines 47-51

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:47-51`

```kotlin
⚪   47 | 
⚪   48 |     fun onSignInSubmit() {
🟡   49 |         val step = _uiState.value.step as? AccountUiState.Step.SignIn ?: return
🟢   50 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢   51 |         viewModelScope.launch {
```

## Lines 70-74

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:70-74`

```kotlin
⚪   70 |     fun onSignUpButtonClicked() {
🟢   71 |         _uiState.update { state ->
🟡   72 |             if (state.step !is AccountUiState.Step.SignIn) return
🟢   73 |             state.copy(step = AccountUiState.Step.SignUp())
⚪   74 |         }
```

## Lines 84-88

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:84-88`

```kotlin
⚪   84 |     fun onEmailChanged(email: String) {
🟢   85 |         _uiState.update { state ->
🟡   86 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢   87 |             state.copy(step = step.copy(email = email))
⚪   88 |         }
```

## Lines 91-95

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:91-95`

```kotlin
⚪   91 |     fun onPasswordChanged(password: String) {
🟢   92 |         _uiState.update { state ->
🟡   93 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢   94 |             state.copy(step = step.copy(password = password))
⚪   95 |         }
```

## Lines 98-102

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:98-102`

```kotlin
⚪   98 |     fun onConfirmPasswordChanged(confirmPassword: String) {
🟢   99 |         _uiState.update { state ->
🟡  100 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢  101 |             state.copy(step = step.copy(confirmPassword = confirmPassword))
⚪  102 |         }
```

## Lines 104-108

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:104-108`

```kotlin
⚪  104 | 
⚪  105 |     fun onSignUpSubmit() {
🟡  106 |         val step = _uiState.value.step as? AccountUiState.Step.SignUp ?: return
🟢  107 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢  108 |         viewModelScope.launch {
```

## Lines 121-125

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:121-125`

```kotlin
⚪  121 |     fun onCodeChanged(code: String) {
🟢  122 |         _uiState.update { state ->
🟡  123 |             val step = state.step as? AccountUiState.Step.VerifyEmail ?: return
🟢  124 |             state.copy(step = step.copy(code = code))
⚪  125 |         }
```

## Lines 127-131

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:127-131`

```kotlin
⚪  127 | 
⚪  128 |     fun onVerifyEmailSubmit() {
🟡  129 |         val step = _uiState.value.step as? AccountUiState.Step.VerifyEmail ?: return
🟢  130 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢  131 |         viewModelScope.launch {
```

## Lines 147-151

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:147-151`

```kotlin
⚪  147 | 
⚪  148 |     fun onInvalidCredentialsErrorDismissed() {
🔴  149 |         _uiState.update { it.copy(showInvalidCredentialsError = false) }
⚪  150 |     }
⚪  151 | }
```
