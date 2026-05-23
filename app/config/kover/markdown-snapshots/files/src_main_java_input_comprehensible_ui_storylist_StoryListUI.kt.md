# src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 59-69

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:59-69`

```kotlin
⚪   59 |     onTextAdventureStarted: (id: String) -> Unit,
🟢   60 |     viewModel: StoryListViewModel = viewModel(),
🟡   61 | ) {
🟢   62 |     val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryListUiState.INITIAL)
🟢   63 |     LaunchedEffect(viewModel.events) {
🟢   64 |         viewModel.events.collect { event ->
🔴   65 |             when (event) {
🔴   66 |                 is StoryListEvent.TextAdventureStarted ->
🔴   67 |                     onTextAdventureStarted(event.adventureId)
⚪   68 |             }
⚪   69 |         }
```

## Lines 91-95

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:91-95`

```kotlin
⚪   91 |     onStartTextAdventure: () -> Unit,
⚪   92 |     state: StoryListUiState,
🟡   93 | ) {
🟢   94 |     val itemsWithIndex = remember(state.items) { state.items.withIndex().toList() }
🟢   95 |     StoryListScaffold(
```

## Lines 122-126

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:122-126`

```kotlin
🟢  122 |                 val shouldAddTopPadding = column % 2 == 0
🟢  123 |                 val shouldAddBottomPadding =
🟡  124 |                     (column % 2 == 0) && it.index != itemsWithIndex.lastIndex
🟢  125 |                 val itemModifier = Modifier.padding(
🟢  126 |                     top = if (shouldAddTopPadding) 0.dp else 140.dp,
```

## Lines 130-139

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:130-139`

```kotlin
🟢  130 |                     is StoryListUiState.StoryListItem.Story -> StoryListItem(
🟢  131 |                         modifier = itemModifier,
🟡  132 |                         onClick = { onStorySelected(item) },
🟢  133 |                         story = item,
⚪  134 |                     )
🟢  135 |                     is StoryListUiState.StoryListItem.TextAdventure -> TextAdventureListItem(
🟢  136 |                         modifier = itemModifier,
🟡  137 |                         onClick = { onTextAdventureSelected(item) },
🟢  138 |                         adventure = item,
⚪  139 |                     )
```

## Lines 159-163

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:159-163`

```kotlin
⚪  159 |     onTranslationLanguageSelected: (LanguageSelection) -> Unit,
⚪  160 |     content: @Composable (paddingValues: PaddingValues, columns: Int) -> Unit
🟡  161 | ) {
🟢  162 |     val columns = if (windowSizeClass.isCompact) 2 else 4
🟢  163 |     Scaffold(
```

## Lines 189-193

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:189-193`

```kotlin
⚪  189 |     onClick: () -> Unit,
⚪  190 |     story: StoryListUiState.StoryListItem.Story,
🟡  191 | ) {
🟢  192 |     Card(
🟢  193 |         modifier = modifier,
```

## Lines 215-219

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:215-219`

```kotlin
⚪  215 |     onClick: () -> Unit,
⚪  216 |     adventure: StoryListUiState.StoryListItem.TextAdventure,
🟡  217 | ) {
🟢  218 |     Card(
🟢  219 |         modifier = modifier,
```

## Lines 250-256

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:250-256`

```kotlin
🟢  250 |     modifier: Modifier = Modifier,
⚪  251 |     adventure: StoryListUiState.StoryListItem.TextAdventure,
🟡  252 | ) {
🟡  253 |     val subtitle = if (adventure.isComplete) {
🔴  254 |         stringResource(R.string.text_adventure_list_complete)
🟢  255 |     } else {
🟢  256 |         stringResource(R.string.text_adventure_list_in_progress)
```

## Lines 267-271

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:267-271`

```kotlin
🟢  267 |     modifier: Modifier = Modifier,
⚪  268 |     onClick: () -> Unit,
🟡  269 | ) {
🟢  270 |     Card(
🟢  271 |         modifier = modifier.testTag("text_adventure_start_button"),
```

## Lines 306-310

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListUI.kt:306-310`

```kotlin
⚪  306 |     image: ImageBitmap,
🟢  307 |     contentDescription: String? = null,
🟡  308 | ) {
🟢  309 |     Box(modifier, propagateMinConstraints = true) {
🟢  310 |         Image(
```
