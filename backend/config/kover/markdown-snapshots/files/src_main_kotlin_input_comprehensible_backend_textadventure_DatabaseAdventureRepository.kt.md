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

## Lines 53-57

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:53-57`

```kotlin
🟢   53 |             .where { AdventuresTable.id eq adventureId }
🟢   54 |             .singleOrNull()
🟡   55 |             ?.get(AdventuresTable.internalPlan)
🟢   56 |             ?.let { plan -> json.decodeFromString<TextAdventureWorldPlan>(plan) }
⚪   57 |     }
```
