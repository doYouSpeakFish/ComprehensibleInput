# src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 75-79

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:75-79`

```kotlin
🟢   75 |     viewModel: StoryReaderViewModel = viewModel { StoryReaderViewModel(storyId) },
⚪   76 |     onErrorDismissed: () -> Unit,
🟡   77 | ) {
🟢   78 |     val state by viewModel.state.collectAsStateWithLifecycle(initialValue = StoryReaderUiState.Loading)
🟢   79 |     StoryReader(
```

## Lines 101-105

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:101-105`

```kotlin
⚪  101 |     onPartScrolledTo: () -> Unit,
⚪  102 |     state: StoryReaderUiState,
🟡  103 | ) {
🟢  104 |     Scaffold(modifier) { paddingValues ->
🟢  105 |         Box(
```

## Lines 127-131

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:127-131`

```kotlin
⚪  127 | 
🟢  128 |                 StoryReaderUiState.Error -> Unit
🔴  129 |             }
⚪  130 | 
🟢  131 |             if (state is StoryReaderUiState.Error) {
```

## Lines 147-153

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:147-153`

```kotlin
⚪  147 |     onPartScrolledTo: () -> Unit,
⚪  148 |     state: StoryReaderUiState.Loaded,
🟡  149 | ) {
🟢  150 |     var timesExplainerTapped by rememberSaveable { mutableIntStateOf(0) }
🟡  151 |     val isExplainerShownAtStart = timesExplainerTapped < 11
🟢  152 |     val currentPartIndex = remember(state.parts, state.currentPartId) {
🟢  153 |         state.parts.indexOfFirst { part -> part.id == state.currentPartId }.coerceAtLeast(0)
```

## Lines 159-164

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:159-164`

```kotlin
🟢  159 |         pageCount = { state.parts.size },
🟢  160 |         onNewPageSettled = { pageIndex ->
🟡  161 |             if (pageIndex == state.scrollingToPage) onPartScrolledTo()
🟡  162 |             currentParts.getOrNull(pageIndex)?.id?.let(onCurrentPartChanged)
⚪  163 |         },
⚪  164 |     )
```

## Lines 171-175

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:171-175`

```kotlin
🟢  171 |                 .testTag("story_reader_pager"),
🟢  172 |             state = pagerState,
🟡  173 |             key = { index -> state.parts.getOrNull(index)?.id ?: index },
🟢  174 |             pageSpacing = 16.dp,
🟢  175 |         ) { pageIndex ->
```

## Lines 213-217

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:213-217`

```kotlin
⚪  213 |     pageIndex: Int,
⚪  214 |     currentlyVisiblePageIndex: Int,
🟡  215 | ) {
🟢  216 |     val isFirstPart = partIndex == 0
🟢  217 |     val isCurrentPart = partIndex == currentlyVisiblePageIndex
```

## Lines 221-225

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:221-225`

```kotlin
🟢  221 |     LaunchedEffect(pageIndex, currentlyVisiblePageIndex) {
🟢  222 |         when {
🟡  223 |             listState.layoutInfo.totalItemsCount == 0 -> {}
🟢  224 |             pageIndex < currentlyVisiblePageIndex -> listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
🟢  225 |             pageIndex > currentlyVisiblePageIndex -> listState.scrollToItem(0)
```

## Lines 242-246

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:242-246`

```kotlin
🟢  242 |         state = listState,
🟢  243 |         verticalArrangement = Arrangement.spacedBy(24.dp)
🟡  244 |     ) {
🟢  245 |         if (isFirstPart) {
🟢  246 |             item {
```

## Lines 269-277

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:269-277`

```kotlin
🟢  269 |                 ?.takeIf {
🟢  270 |                     contentIndex == selectedSentence.paragraphIndex &&
🟡  271 |                             partIndex == selectedSentence.partIndex
⚪  272 |                 }
🟢  273 |             val choiceSelectionIndex = selectedChoice?.optionIndex
🟢  274 |                 ?.takeIf {
🟡  275 |                     partIndex == selectedChoice.partIndex && item is StoryContentPartUiState.Choices
⚪  276 |                 }
🟢  277 |             val isSelectionTranslated = when {
```

## Lines 285-289

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:285-289`

```kotlin
🟢  285 |                 selectedChoiceIndex = choiceSelectionIndex,
🟢  286 |                 isSelectionTranslated = isSelectionTranslated,
🟡  287 |                 onSentenceSelected = { sentenceIndex ->
🟢  288 |                     onSentenceSelected(partIndex, contentIndex, sentenceIndex)
⚪  289 |                 },
```

## Lines 294-303

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:294-303`

```kotlin
🟢  294 |         }
⚪  295 | 
🟡  296 |         if (!isExplainerShownAtStart) {
🔴  297 |             item {
🔴  298 |                 TranslateExplainer(
🔴  299 |                     modifier = Modifier.padding(vertical = 16.dp),
🔴  300 |                     onExplainerTapped = onExplainerTapped,
🔴  301 |                     timesExplainerTapped = timesExplainerTapped,
⚪  302 |                 )
⚪  303 |             }
```

## Lines 358-365

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:358-365`

```kotlin
🟢  358 |                 .clickable(
🟢  359 |                     onClick = onExplainerTapped,
🟡  360 |                     enabled = timesExplainerTapped < explainerMessages.lastIndex,
⚪  361 |                 )
🟢  362 |                 .animateContentSize(),
🟡  363 |             text = explainerMessages.getOrElse(timesExplainerTapped) { explainerMessages.last() },
🟢  364 |             style = MaterialTheme.typography.labelLarge,
🟢  365 |             color = MaterialTheme.colorScheme.background,
```

## Lines 370-374

Location: `src/main/java/input/comprehensible/ui/storyreader/StoryReaderUI.kt:370-374`

```kotlin
⚪  370 | @OptIn(ExperimentalMaterial3Api::class)
⚪  371 | @Composable
🟡  372 | private fun StoryReaderErrorDialog(onDismissRequest: () -> Unit) {
🟢  373 |     BasicAlertDialog(onDismissRequest = onDismissRequest) {
🟢  374 |         Surface(
```
