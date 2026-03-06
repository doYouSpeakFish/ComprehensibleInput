# src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 32-36

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:32-36`

```kotlin
⚪   32 |     ) { adventureResult, selectedSentence, inputTextValue ->
🟢   33 |         when (adventureResult) {
🟡   34 |             TextAdventureResult.Error -> TextAdventureUiState.Error
🟢   35 |             TextAdventureResult.Loading -> TextAdventureUiState.Loading
🟢   36 |             is TextAdventureResult.Success -> {
```

## Lines 72-83

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:72-83`

```kotlin
⚪   72 |     fun onSendMessage() {
🟢   73 |         val message = inputText.value.trim()
🟡   74 |         if (message.isBlank()) return
🟢   75 |         val currentState = state.value
🟡   76 |         if (currentState is TextAdventureUiState.Loaded && !currentState.isSendEnabled) return
🟢   77 |         inputText.value = ""
🟢   78 |         viewModelScope.launch {
🟢   79 |             continueTextAdventureUseCase(adventureId = adventureId, userMessage = message)
⚪   80 |         }
⚪   81 |     }
⚪   82 |
⚪   83 |     fun onRetry() {
```

## Lines 83-90

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:83-90`

```kotlin
⚪   83 |     fun onRetry() {
🟢   84 |         viewModelScope.launch {
🟢   85 |             continueTextAdventureUseCase.retry(adventureId = adventureId)
⚪   86 |         }
⚪   87 |     }
```

## Lines 89-104

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:89-104`

```kotlin
⚪   89 |     fun onSentenceSelected(messageId: String, paragraphIndex: Int, sentenceIndex: Int) {
🟢   90 |         selectedText.update { selectedSentence ->
🟡   91 |             if (selectedSentence?.messageId == messageId &&
🔴   92 |                 selectedSentence.paragraphIndex == paragraphIndex &&
🔴   93 |                 selectedSentence.sentenceIndex == sentenceIndex
⚪   94 |             ) {
🔴   95 |                 return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
⚪   96 |             }
🟢   97 |             TextAdventureUiState.SelectedText(
```
