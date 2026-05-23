# src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 41-60

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:41-60`

```kotlin
⚪   41 | 
⚪   42 | @Composable
🔴   43 | fun TextAdventureScreen(
🔴   44 |     modifier: Modifier = Modifier,
⚪   45 |     adventureId: String,
⚪   46 |     onNavigateUp: () -> Unit,
🔴   47 |     viewModel: TextAdventureViewModel = viewModel { TextAdventureViewModel(adventureId) },
🔴   48 | ) {
🔴   49 |     val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TextAdventureUiState.Loading)
🔴   50 |     TextAdventureScreen(
🔴   51 |         modifier = modifier,
🔴   52 |         onNavigateUp = onNavigateUp,
🔴   53 |         onInputChanged = viewModel::onInputChanged,
🔴   54 |         onSendMessage = viewModel::onSendMessage,
🔴   55 |         onSentenceSelected = viewModel::onSentenceSelected,
🔴   56 |         state = state,
⚪   57 |     )
🔴   58 | }
⚪   59 | 
⚪   60 | @Composable
```

## Lines 66-70

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:66-70`

```kotlin
⚪   66 |     onSentenceSelected: (messageId: String, paragraphIndex: Int, sentenceIndex: Int) -> Unit,
⚪   67 |     state: TextAdventureUiState,
🟡   68 | ) {
🟢   69 |     Scaffold(
🟢   70 |         modifier = modifier,
```

## Lines 76-80

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:76-80`

```kotlin
⚪   76 |         },
🟢   77 |         bottomBar = {
🟡   78 |             if (state is TextAdventureUiState.Loaded && state.isInputEnabled) {
🟢   79 |                 TextAdventureInput(
🟢   80 |                     inputText = state.inputText,
```

## Lines 91-99

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:91-99`

```kotlin
⚪   91 |         ) {
🟢   92 |             when (state) {
🟡   93 |                 TextAdventureUiState.Error -> Unit
🟡   94 |                 TextAdventureUiState.Loading -> CircularProgressIndicator(
🔴   95 |                     modifier = Modifier
🔴   96 |                         .align(Alignment.Center)
🔴   97 |                         .testTag("text_adventure_loading"),
⚪   98 |                 )
🟢   99 |                 is TextAdventureUiState.Loaded -> TextAdventureMessages(
```

## Lines 102-106

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:102-106`

```kotlin
🟢  102 |                     onSentenceSelected = onSentenceSelected,
⚪  103 |                 )
🔴  104 |             }
🟢  105 |         }
⚪  106 |     }
```

## Lines 112-116

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:112-116`

```kotlin
⚪  112 |     state: TextAdventureUiState.Loaded,
⚪  113 |     onSentenceSelected: (messageId: String, paragraphIndex: Int, sentenceIndex: Int) -> Unit,
🟡  114 | ) {
🟢  115 |     LazyColumn(
🟢  116 |         modifier = modifier
```

## Lines 144-148

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:144-148`

```kotlin
⚪  144 |     selectedText: TextAdventureUiState.SelectedText?,
⚪  145 |     onSentenceSelected: (messageId: String, paragraphIndex: Int, sentenceIndex: Int) -> Unit,
🟡  146 | ) {
🟢  147 |     Surface(
🟢  148 |         modifier = Modifier.fillMaxWidth(),
```

## Lines 152-161

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:152-161`

```kotlin
🟢  152 |         Column(modifier = Modifier.padding(16.dp)) {
🟢  153 |             message.paragraphs.forEachIndexed { paragraphIndex, paragraph ->
🟡  154 |                 val isSelectedParagraph = selectedText?.messageId == message.id &&
🔴  155 |                     selectedText.paragraphIndex == paragraphIndex
🟢  156 |                 val selectedSentenceIndex =
🟡  157 |                     selectedText?.sentenceIndex?.takeIf { isSelectedParagraph }
🟢  158 |                 val isSelectionTranslated =
🟡  159 |                     selectedText?.isTranslated?.takeIf { isSelectedParagraph } ?: false
🟢  160 |                 StoryContentPart(
🟢  161 |                     state = paragraph,
```

## Lines 163-175

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:163-175`

```kotlin
🟢  163 |                     isSelectionTranslated = isSelectionTranslated,
🟢  164 |                     onSentenceSelected = { sentenceIndex ->
🔴  165 |                         onSentenceSelected(message.id, paragraphIndex, sentenceIndex)
⚪  166 |                     },
⚪  167 |                 )
🟢  168 |             }
🟡  169 |             if (message.isEnding) {
🔴  170 |                 Text(
🔴  171 |                     modifier = Modifier.padding(top = 12.dp),
🔴  172 |                     text = stringResource(R.string.text_adventure_ending_label),
🔴  173 |                     style = MaterialTheme.typography.labelMedium,
⚪  174 |                 )
🟢  175 |             }
```

## Lines 179-183

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:179-183`

```kotlin
⚪  179 | 
⚪  180 | @Composable
🟡  181 | private fun UserMessage(message: TextAdventureMessageUiState.User) {
🟢  182 |     Box(
🟢  183 |         modifier = Modifier.fillMaxWidth(),
```

## Lines 201-205

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:201-205`

```kotlin
⚪  201 |     onInputChanged: (String) -> Unit,
⚪  202 |     onSendMessage: () -> Unit,
🟡  203 | ) {
🟢  204 |     Surface(
🟢  205 |         tonalElevation = 2.dp,
```
