# src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 35-40

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:35-40`

```kotlin
⚪   35 |         }
🟢   36 |         .catch { throwable ->
🔴   37 |             Timber.e(throwable, "Failed to load text adventures list")
🔴   38 |             emit(TextAdventuresListResult.Error)
⚪   39 |         }
⚪   40 | 
```

## Lines 42-47

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:42-47`

```kotlin
🟢   42 |         .getAdventureSentenceRows(id)
🟢   43 |         .map { rows ->
🟡   44 |             if (rows.isEmpty()) {
🔴   45 |                 TextAdventureResult.Error
⚪   46 |             } else {
🟢   47 |                 TextAdventureResult.Success(rows.toDomain())
```

## Lines 50-55

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:50-55`

```kotlin
🟢   50 |         .distinctUntilChanged()
🟢   51 |         .catch { throwable ->
🔴   52 |         Timber.e(throwable, "Failed to load text adventure %s", id)
🔴   53 |         emit(TextAdventureResult.Error)
⚪   54 |     }
⚪   55 | 
```

## Lines 89-94

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:89-94`

```kotlin
⚪   89 | 
⚪   90 |     suspend fun respondToAdventure(adventureId: String, userMessage: String) {
🟡   91 |         val adventure = localDataSource.getAdventureSnapshot(adventureId) ?: run {
🔴   92 |             Timber.e("Text adventure %s not found when responding", adventureId)
🟢   93 |             return
⚪   94 |         }
```

## Lines 97-101

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:97-101`

```kotlin
🟢   97 |             translationLanguage = adventure.translationLanguage,
⚪   98 |         )
🟡   99 |         val nextIndex = (localDataSource.getLatestMessageIndex(adventureId) ?: -1) + 1
🟢  100 |         val now = clock()
🟢  101 |         insertUserMessage(
```

## Lines 215-219

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:215-219`

```kotlin
⚪  215 | sealed interface TextAdventuresListResult {
🟢  216 |     data class Success(val adventures: List<TextAdventureSummary>) : TextAdventuresListResult
🔴  217 |     object Error : TextAdventuresListResult
⚪  218 | }
⚪  219 | 
```

## Lines 249-253

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:249-253`

```kotlin
🟢  249 |         translationLanguage = adventure.translationLanguage,
🟢  250 |         messages = messageUi,
🟡  251 |         isComplete = messageUi.lastOrNull()?.isEnding == true,
⚪  252 |     )
⚪  253 | }
```

## Lines 270-277

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:270-277`

```kotlin
🟢  270 |     val sentencesByLanguage = sortedBy { it.sentenceIndex }
🟢  271 |         .groupBy { it.language }
🟡  272 |     val adventureId = firstOrNull()?.adventureId.orEmpty()
🟢  273 |     return TextAdventureParagraph(
🟢  274 |         id = "${adventureId}-${messageIndex}-$paragraphIndex",
🟡  275 |         sentences = sentencesByLanguage[learningLanguage].orEmpty().map { it.text },
🟢  276 |         translatedSentences = sentencesByLanguage[translationLanguage].orEmpty().map { it.text },
⚪  277 |     )
```

## Lines 297-301

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:297-301`

```kotlin
🟢  297 |         .groupBy { it.messageIndex }
🟢  298 |     return messages.mapNotNull { message ->
🟡  299 |         val text = sentencesByMessageIndex[message.messageIndex]
🟢  300 |             .orEmpty()
🟢  301 |             .groupBy { it.paragraphIndex }
```

## Lines 306-314

Location: `src/main/java/input/comprehensible/data/textadventures/TextAdventuresRepository.kt:306-314`

```kotlin
🟢  306 |                     .joinToString(" ") { it.text.trim() }
🟢  307 |                     .trim()
🟡  308 |                 sentenceText.takeIf { it.isNotBlank() }
⚪  309 |             }
🟢  310 |             .joinToString("\n\n")
🟢  311 |             .trim()
🟡  312 |         text.takeIf { it.isNotBlank() }?.let {
🟢  313 |             TextAdventureHistoryMessage(
🟢  314 |                 role = when (message.sender) {
```
