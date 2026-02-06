# app/src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported


## Lines 51-55

Location: `app/src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:51-55`

```kotlin
⚪   51 |     onSettingsClick: () -> Unit,
🟢   52 |     viewModel: StoryListViewModel = viewModel(),
🟡   53 | ) {
🟢   54 |     val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryListUiState.INITIAL)
🟢   55 |     StoryListScreen(
```

## Lines 71-75

Location: `app/src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:71-75`

```kotlin
⚪   71 |     onTranslationLanguageSelected: (LanguageSelection) -> Unit,
⚪   72 |     state: StoryListUiState,
🟡   73 | ) {
🟢   74 |     val storiesWithIndex = remember(state.stories) { state.stories.withIndex().toList() }
🟢   75 |     StoryListScaffold(
```

## Lines 121-126

Location: `app/src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:121-126`

```kotlin
⚪  121 |     onTranslationLanguageSelected: (LanguageSelection) -> Unit,
⚪  122 |     content: @Composable (paddingValues: PaddingValues, columns: Int) -> Unit
🟡  123 | ) {
🟡  124 |     val columns = if (windowSizeClass.isCompact) 2 else 4
🟢  125 |     Scaffold(
🟢  126 |         modifier = modifier,
```

## Lines 151-155

Location: `app/src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:151-155`

```kotlin
⚪  151 |     onClick: () -> Unit,
⚪  152 |     story: StoryListUiState.StoryListItem,
🟡  153 | ) {
🟢  154 |     Card(
🟢  155 |         modifier = modifier,
```

## Lines 177-181

Location: `app/src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:177-181`

```kotlin
⚪  177 |     image: ImageBitmap,
🟢  178 |     contentDescription: String? = null,
🟡  179 | ) {
🟢  180 |     Box(modifier, propagateMinConstraints = true) {
🟢  181 |         Image(
```

