# src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 27-34

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:27-34`

```kotlin
⚪   27 |                 } else {
🟢   28 |                     _uiState.update { state ->
🟡   29 |                         if (state.step is AccountUiState.Step.Loading || state.step is AccountUiState.Step.SignedIn) {
🟢   30 |                             AccountUiState(step = AccountUiState.Step.SignIn())
⚪   31 |                         } else {
🔴   32 |                             state
🟢   33 |                         }
⚪   34 |                     }
```

## Lines 40-44

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:40-44`

```kotlin
⚪   40 |     fun onSignInEmailChanged(email: String) {
🟢   41 |         _uiState.update { state ->
🟡   42 |             val step = state.step as? AccountUiState.Step.SignIn ?: return
🟢   43 |             state.copy(step = step.copy(email = email))
⚪   44 |         }
```

## Lines 47-51

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:47-51`

```kotlin
⚪   47 |     fun onSignInPasswordChanged(password: String) {
🟢   48 |         _uiState.update { state ->
🟡   49 |             val step = state.step as? AccountUiState.Step.SignIn ?: return
🟢   50 |             state.copy(step = step.copy(password = password))
⚪   51 |         }
```

## Lines 53-57

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:53-57`

```kotlin
⚪   53 | 
⚪   54 |     fun onSignInSubmit() {
🟡   55 |         val step = _uiState.value.step as? AccountUiState.Step.SignIn ?: return
🟢   56 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢   57 |         viewModelScope.launch {
```

## Lines 73-77

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:73-77`

```kotlin
⚪   73 |     fun onSignUpButtonClicked() {
🟢   74 |         _uiState.update { state ->
🟡   75 |             if (state.step !is AccountUiState.Step.SignIn) return
🟢   76 |             state.copy(step = AccountUiState.Step.SignUp())
⚪   77 |         }
```

## Lines 86-90

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:86-90`

```kotlin
⚪   86 |     fun onEmailChanged(email: String) {
🟢   87 |         _uiState.update { state ->
🟡   88 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢   89 |             state.copy(step = step.copy(email = email))
⚪   90 |         }
```

## Lines 93-97

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:93-97`

```kotlin
⚪   93 |     fun onPasswordChanged(password: String) {
🟢   94 |         _uiState.update { state ->
🟡   95 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢   96 |             state.copy(step = step.copy(password = password))
⚪   97 |         }
```

## Lines 100-104

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:100-104`

```kotlin
⚪  100 |     fun onConfirmPasswordChanged(confirmPassword: String) {
🟢  101 |         _uiState.update { state ->
🟡  102 |             val step = state.step as? AccountUiState.Step.SignUp ?: return
🟢  103 |             state.copy(step = step.copy(confirmPassword = confirmPassword))
⚪  104 |         }
```

## Lines 106-110

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:106-110`

```kotlin
⚪  106 | 
⚪  107 |     fun onSignUpSubmit() {
🟡  108 |         val step = _uiState.value.step as? AccountUiState.Step.SignUp ?: return
🟢  109 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢  110 |         viewModelScope.launch {
```

## Lines 123-127

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:123-127`

```kotlin
⚪  123 |     fun onCodeChanged(code: String) {
🟢  124 |         _uiState.update { state ->
🟡  125 |             val step = state.step as? AccountUiState.Step.VerifyEmail ?: return
🟢  126 |             state.copy(step = step.copy(code = code))
⚪  127 |         }
```

## Lines 129-133

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:129-133`

```kotlin
⚪  129 | 
⚪  130 |     fun onVerifyEmailSubmit() {
🟡  131 |         val step = _uiState.value.step as? AccountUiState.Step.VerifyEmail ?: return
🟢  132 |         _uiState.update { it.copy(step = step.copy(isLoading = true)) }
🟢  133 |         viewModelScope.launch {
```

## Lines 149-153

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountViewModel.kt:149-153`

```kotlin
⚪  149 | 
⚪  150 |     fun onInvalidCredentialsErrorDismissed() {
🔴  151 |         _uiState.update { it.copy(showInvalidCredentialsError = false) }
⚪  152 |     }
⚪  153 | }
```
