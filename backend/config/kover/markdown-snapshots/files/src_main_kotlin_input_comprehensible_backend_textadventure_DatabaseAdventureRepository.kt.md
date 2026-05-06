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

## Lines 97-101

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:97-101`

```kotlin
🟢   97 |         .where { AdventuresTable.id eq adventureId }
🟢   98 |         .singleOrNull()
🟡   99 |         ?.get(AdventuresTable.inputTokensUsed)
🟢  100 |         ?: 0L
⚪  101 | 
```

## Lines 104-108

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:104-108`

```kotlin
🟢  104 |         .where { AdventuresTable.id eq adventureId }
🟢  105 |         .singleOrNull()
🟡  106 |         ?.get(AdventuresTable.outputTokensUsed)
🟢  107 |         ?: 0L
⚪  108 | 
```
