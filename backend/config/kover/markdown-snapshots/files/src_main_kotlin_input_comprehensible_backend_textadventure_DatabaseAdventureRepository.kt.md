# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 42-46

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:42-46`

```kotlin
🟢   42 |         val messages = findMessageRows(adventureId).map { messageRow ->
🟢   43 |             messageRow.toRemoteMessage(
🟡   44 |                 sentencesForMessage = sentenceRowsByMessage[messageRow[AdventureMessagesTable.messageIndex]].orEmpty(),
🟢   45 |                 learningLanguage = adventureRow[AdventuresTable.learningLanguage],
🟢   46 |                 translationLanguage = adventureRow[AdventuresTable.translationLanguage],
```

## Lines 64-68

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:64-68`

```kotlin
🟢   64 |             .where { AdventuresTable.id eq adventureId }
🟢   65 |             .singleOrNull()
🟡   66 |             ?.get(AdventuresTable.adventurePlan)
⚪   67 |     }
⚪   68 | 
```
