# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 41-45

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:41-45`

```kotlin
🟢   41 |         val messages = findMessageRows(adventureId).map { messageRow ->
🟢   42 |             messageRow.toRemoteMessage(
🟡   43 |                 sentencesForMessage = sentenceRowsByMessage[messageRow[AdventureMessagesTable.messageIndex]].orEmpty(),
🟢   44 |                 learningLanguage = adventureRow[AdventuresTable.learningLanguage],
🟢   45 |                 translationLanguage = adventureRow[AdventuresTable.translationLanguage],
```

## Lines 63-67

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:63-67`

```kotlin
🟢   63 |             .where { AdventuresTable.id eq adventureId }
🟢   64 |             .singleOrNull()
🟡   65 |             ?.get(AdventuresTable.adventurePlan)
⚪   66 |     }
⚪   67 | 
```
