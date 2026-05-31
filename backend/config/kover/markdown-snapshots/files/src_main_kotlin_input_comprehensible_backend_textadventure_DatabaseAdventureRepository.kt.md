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

## Lines 77-81

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:77-81`

```kotlin
🟢   77 |             val now = nowProvider()
🟢   78 |             val existingCreatedAt = findAdventureCreatedAt(adventureId)
🟡   79 |             val existingTitle = findAdventureRow(adventureId)?.get(AdventuresTable.title).orEmpty()
🟢   80 |             AdventuresTable.upsert {
🟢   81 |                 it[id] = adventureId
```

## Lines 84-88

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:84-88`

```kotlin
🟢   84 |                 it[this.translationLanguage] = translationLanguage
🟢   85 |                 it[this.title] = existingTitle
🟡   86 |                 it[createdAt] = existingCreatedAt ?: now
🟢   87 |                 it[updatedAt] = now
⚪   88 |             }
```
