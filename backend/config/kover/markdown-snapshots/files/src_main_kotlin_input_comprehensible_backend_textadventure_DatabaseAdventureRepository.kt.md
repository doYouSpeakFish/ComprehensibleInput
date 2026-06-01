# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 66-70

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:66-70`

```kotlin
🟢   66 |         val messages = findMessageRows(adventureId).map { messageRow ->
🟢   67 |             messageRow.toRemoteMessage(
🟡   68 |                 sentencesForMessage = sentenceRowsByMessage[messageRow[AdventureMessagesTable.id]].orEmpty(),
🟢   69 |                 learningLanguage = adventureRow[AdventuresTable.learningLanguage],
🟢   70 |                 translationLanguage = adventureRow[AdventuresTable.translationLanguage],
```

## Lines 77-82

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:77-82`

```kotlin
⚪   77 |     override fun appendUserMessage(message: PersistedUserAdventureMessage): TextAdventureMessageRemoteResponse? =
🟢   78 |         transaction(database) {
🟡   79 |             val adventureRow = findAdventureRow(message.adventureId) ?: return@transaction null
🟡   80 |             if (adventureRow[AdventuresTable.accountId] != message.accountId) return@transaction null
🟢   81 |             if (!messageExists(adventureId = message.adventureId, messageId = message.parentMessageId)) {
🟢   82 |                 return@transaction null
```

## Lines 117-121

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:117-121`

```kotlin
🟢  117 |                 sentences = listOf(message.userMessage),
⚪  118 |             )
🟡  119 |             findMessageRow(message.messageId)?.toRemoteMessage(
🟢  120 |                 sentencesForMessage = findSentenceRowsForMessage(message.messageId),
🟢  121 |                 learningLanguage = message.learningLanguage,
```
