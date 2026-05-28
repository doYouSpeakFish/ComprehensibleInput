# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 58-62

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:58-62`

```kotlin
🟢   58 |         val messages = findMessageRows(adventureId).map { messageRow ->
🟢   59 |             messageRow.toRemoteMessage(
🟡   60 |                 sentencesForMessage = sentenceRowsByMessage[messageRow[AdventureMessagesTable.messageIndex]].orEmpty(),
🟢   61 |                 learningLanguage = adventureRow[AdventuresTable.learningLanguage],
🟢   62 |                 translationLanguage = adventureRow[AdventuresTable.translationLanguage],
```
