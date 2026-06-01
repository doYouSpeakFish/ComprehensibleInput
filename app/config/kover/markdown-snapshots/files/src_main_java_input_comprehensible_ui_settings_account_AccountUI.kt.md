# src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 38-42

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:38-42`

```kotlin
🟢   38 |     modifier: Modifier = Modifier,
🟢   39 |     viewModel: AccountViewModel = viewModel(),
🟡   40 | ) {
🟢   41 |     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
🟢   42 |     AccountScreen(
```

## Lines 66-70

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:66-70`

```kotlin
⚪   66 |     onErrorDismissed: () -> Unit,
🟢   67 |     modifier: Modifier = Modifier,
🟡   68 | ) {
🟢   69 |     Scaffold(
🟢   70 |         modifier = modifier,
```

## Lines 95-99

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:95-99`

```kotlin
⚪   95 |                 )
🟢   96 |                 is AccountUiState.Step.Verified -> VerifiedStep()
🔴   97 |             }
🟢   98 |         }
⚪   99 |     }
```

## Lines 112-116

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:112-116`

```kotlin
⚪  112 |     onSubmit: () -> Unit,
🟢  113 |     modifier: Modifier = Modifier,
🟡  114 | ) {
🟢  115 |     Column(
🟢  116 |         modifier = modifier
```

## Lines 225-229

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:225-229`

```kotlin
⚪  225 | private fun VerifiedStep(
🟢  226 |     modifier: Modifier = Modifier,
🟡  227 | ) {
🟢  228 |     Box(
🟢  229 |         modifier = modifier
```
