# src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 46-50

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:46-50`

```kotlin
⚪   46 |     onNavigateUp: () -> Unit,
🟢   47 |     viewModel: TextAdventureViewModel = viewModel { TextAdventureViewModel(adventureId) },
🟡   48 | ) {
🟢   49 |     val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TextAdventureUiState.Loading)
🟢   50 |     TextAdventureScreen(
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

## Lines 91-95

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:91-95`

```kotlin
⚪   91 |         ) {
🟢   92 |             when (state) {
🟡   93 |                 TextAdventureUiState.Error -> Unit
🟢   94 |                 TextAdventureUiState.Loading -> CircularProgressIndicator(
🟢   95 |                     modifier = Modifier
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

## Lines 153-157

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureUI.kt:153-157`

```kotlin
🟢  153 |             message.paragraphs.forEachIndexed { paragraphIndex, paragraph ->
🟢  154 |                 val isSelectedParagraph = selectedText?.messageId == message.id &&
🟡  155 |                     selectedText.paragraphIndex == paragraphIndex
🟢  156 |                 val selectedSentenceIndex =
🟢  157 |                     selectedText?.sentenceIndex?.takeIf { isSelectedParagraph }
```
