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

## Lines 72-87

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:72-87`

```kotlin
⚪   72 |     fun onSendMessage() {
🟢   73 |         val message = inputText.value.trim()
🟡   74 |         if (message.isBlank()) return
🟢   75 |         val currentState = state.value
🟡   76 |         if (currentState is TextAdventureUiState.Loaded && !currentState.isSendEnabled) return
🟢   77 |         inputText.value = ""
🟢   78 |         viewModelScope.launch {
⚪   79 |             try {
🟢   80 |                 continueTextAdventureUseCase(adventureId = adventureId, userMessage = message)
🟡   81 |             } catch (e: CancellationException) {
🟢   82 |                 throw e
🟡   83 |             } catch (e: Exception) {
🟢   84 |                 Timber.e(e, "Failed to send message for adventure %s", adventureId)
⚪   85 |             }
⚪   86 |         }
⚪   87 |     }
```

## Lines 89-99

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:89-99`

```kotlin
⚪   89 |     fun onRetry() {
🟢   90 |         viewModelScope.launch {
⚪   91 |             try {
🟢   92 |                 continueTextAdventureUseCase.retry(adventureId = adventureId)
🟡   93 |             } catch (e: CancellationException) {
🟢   94 |                 throw e
🟡   95 |             } catch (e: Exception) {
🟢   96 |                 Timber.e(e, "Failed to retry adventure %s", adventureId)
⚪   97 |             }
⚪   98 |         }
⚪   99 |     }
```

## Lines 101-116

Location: `src/main/java/input/comprehensible/ui/textadventure/TextAdventureViewModel.kt:101-116`

```kotlin
⚪  101 |     fun onSentenceSelected(messageId: String, paragraphIndex: Int, sentenceIndex: Int) {
🟢  102 |         selectedText.update { selectedSentence ->
🟡  103 |             if (selectedSentence?.messageId == messageId &&
🔴  104 |                 selectedSentence.paragraphIndex == paragraphIndex &&
🔴  105 |                 selectedSentence.sentenceIndex == sentenceIndex
⚪  106 |             ) {
🔴  107 |                 return@update selectedSentence.copy(isTranslated = !selectedSentence.isTranslated)
⚪  108 |             }
🟢  109 |             TextAdventureUiState.SelectedText(
```
