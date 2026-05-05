# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 40-44

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:40-44`

```kotlin
🟢   40 |         val messages = findMessageRows(adventureId).map { messageRow ->
🟢   41 |             messageRow.toRemoteMessage(
🟡   42 |                 sentencesForMessage = sentenceRowsByMessage[messageRow[AdventureMessagesTable.messageIndex]].orEmpty(),
🟢   43 |                 learningLanguage = adventureRow[AdventuresTable.learningLanguage],
🟢   44 |                 translationLanguage = adventureRow[AdventuresTable.translationLanguage],
```

## Lines 54-58

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:54-58`

```kotlin
🟢   54 |             .where { AdventuresTable.id eq adventureId }
🟢   55 |             .singleOrNull()
🟡   56 |             ?.get(AdventuresTable.internalPlan)
⚪   57 |     }
⚪   58 | 
```
