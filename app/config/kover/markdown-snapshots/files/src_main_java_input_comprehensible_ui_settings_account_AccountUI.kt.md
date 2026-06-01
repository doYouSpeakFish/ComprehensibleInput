# src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 46-67

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:46-67`

```kotlin
🟢   46 |     modifier: Modifier = Modifier,
🟢   47 |     viewModel: AccountViewModel = viewModel(),
🟡   48 | ) {
🟢   49 |     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
🟢   50 |     AccountScreen(
🟢   51 |         uiState = uiState,
🟢   52 |         onNavigateUp = onNavigateUp,
🟡   53 |         onSignInEmailChanged = viewModel::onSignInEmailChanged,
🟡   54 |         onSignInPasswordChanged = viewModel::onSignInPasswordChanged,
🟡   55 |         onSignInSubmit = viewModel::onSignInSubmit,
🟡   56 |         onSignUpButtonClicked = viewModel::onSignUpButtonClicked,
🟡   57 |         onSignOutClicked = viewModel::onSignOutClicked,
🟡   58 |         onEmailChanged = viewModel::onEmailChanged,
🟡   59 |         onPasswordChanged = viewModel::onPasswordChanged,
🟡   60 |         onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
🟡   61 |         onSignUpSubmit = viewModel::onSignUpSubmit,
🟡   62 |         onCodeChanged = viewModel::onCodeChanged,
🟡   63 |         onVerifyEmailSubmit = viewModel::onVerifyEmailSubmit,
🟡   64 |         onErrorDismissed = viewModel::onErrorDismissed,
🟡   65 |         onInvalidCredentialsErrorDismissed = viewModel::onInvalidCredentialsErrorDismissed,
🟢   66 |         modifier = modifier,
⚪   67 |     )
```

## Lines 86-90

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:86-90`

```kotlin
⚪   86 |     onInvalidCredentialsErrorDismissed: () -> Unit,
🟢   87 |     modifier: Modifier = Modifier,
🟡   88 | ) {
🟢   89 |     Scaffold(
🟢   90 |         modifier = modifier,
```

## Lines 131-135

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:131-135`

```kotlin
🟢  131 |                     onSubmit = onVerifyEmailSubmit,
⚪  132 |                 )
🔴  133 |             }
🟢  134 |         }
⚪  135 |     }
```

## Lines 149-153

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:149-153`

```kotlin
⚪  149 |     onSignOutClicked: () -> Unit,
🟢  150 |     modifier: Modifier = Modifier,
🟡  151 | ) {
🟢  152 |     Column(
🟢  153 |         modifier = modifier
```

## Lines 180-184

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:180-184`

```kotlin
⚪  180 |     onSignUpClicked: () -> Unit,
🟢  181 |     modifier: Modifier = Modifier,
🟡  182 | ) {
🟢  183 |     Column(
🟢  184 |         modifier = modifier
```

## Lines 247-251

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:247-251`

```kotlin
⚪  247 |     onSubmit: () -> Unit,
🟢  248 |     modifier: Modifier = Modifier,
🟡  249 | ) {
🟢  250 |     Column(
🟢  251 |         modifier = modifier
```

## Lines 362-366

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:362-366`

```kotlin
⚪  362 |     onDismissRequest: () -> Unit,
🟢  363 |     modifier: Modifier = Modifier,
🟡  364 | ) {
🟢  365 |     BasicAlertDialog(onDismissRequest = onDismissRequest) {
🟢  366 |         Surface(
```
