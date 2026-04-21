# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 109-113

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:109-113`

```kotlin
🟢  109 |             .map { messageRow ->
🟢  110 |                 val messageIndex = messageRow[AdventureMessagesTable.messageIndex]
🟡  111 |                 val paragraphs = sentencesByMessage[messageIndex]
🟢  112 |                     .orEmpty()
🟢  113 |                     .groupBy { it[AdventureSentencesTable.paragraphIndex] }
```
