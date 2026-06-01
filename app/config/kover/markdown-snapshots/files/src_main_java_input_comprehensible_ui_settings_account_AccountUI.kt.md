# src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 21-25

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:21-25`

```kotlin
🟢   21 |     modifier: Modifier = Modifier,
🟢   22 |     viewModel: AccountViewModel = viewModel(),
🟡   23 | ) {
🟢   24 |     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
🟢   25 |     AccountScreen(
```

## Lines 35-39

Location: `src/main/java/input/comprehensible/ui/settings/account/AccountUI.kt:35-39`

```kotlin
⚪   35 |     onNavigateUp: () -> Unit,
🟢   36 |     modifier: Modifier = Modifier,
🟡   37 | ) {
🟢   38 |     Scaffold(
🟢   39 |         modifier = modifier,
```
