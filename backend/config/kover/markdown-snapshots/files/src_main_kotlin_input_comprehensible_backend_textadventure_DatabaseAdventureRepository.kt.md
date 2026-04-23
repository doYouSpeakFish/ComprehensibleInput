# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 39-43

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:39-43`

```kotlin
🟢   39 |         val messages = findMessageRows(adventureId).map { messageRow ->
🟢   40 |             messageRow.toRemoteMessage(
🟡   41 |                 sentencesForMessage = sentenceRowsByMessage[messageRow[AdventureMessagesTable.messageIndex]].orEmpty(),
🟢   42 |                 learningLanguage = adventureRow[AdventuresTable.learningLanguage],
🟢   43 |                 translationLanguage = adventureRow[AdventuresTable.translationLanguage],
```
