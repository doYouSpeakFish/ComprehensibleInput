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

## Lines 54-58

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:54-58`

```kotlin
🟢   54 |             .where { AdventuresTable.id eq adventureId }
🟢   55 |             .singleOrNull()
🟡   56 |             ?.get(AdventuresTable.internalPlan)
🟢   57 |             ?.let { plan -> json.decodeFromString<TextAdventureWorldPlan>(plan) }
⚪   58 |     }
```
