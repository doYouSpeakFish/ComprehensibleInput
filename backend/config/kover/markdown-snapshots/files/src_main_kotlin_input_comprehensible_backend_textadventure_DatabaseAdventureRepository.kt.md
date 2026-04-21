# src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 19-145

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:19-145`

```kotlin
⚪   19 | 
⚪   20 | class DatabaseAdventureRepository(
🔴   21 |     private val database: Database,
🔴   22 |     private val nowProvider: () -> Long = { System.currentTimeMillis() },
⚪   23 | ) : AdventureRepository {
⚪   24 |     override fun saveAdventurePart(adventurePart: PersistedAdventurePart) {
🔴   25 |         transaction(database) {
🔴   26 |             val now = nowProvider()
🔴   27 |             val existingCreatedAt = AdventuresTable
🔴   28 |                 .select(AdventuresTable.createdAt)
🔴   29 |                 .where { AdventuresTable.id eq adventurePart.adventureId }
🔴   30 |                 .singleOrNull()
🔴   31 |                 ?.get(AdventuresTable.createdAt)
⚪   32 | 
🔴   33 |             AdventuresTable.upsert {
🔴   34 |                 it[id] = adventurePart.adventureId
🔴   35 |                 it[this.title] = adventurePart.title
🔴   36 |                 it[this.learningLanguage] = adventurePart.learningLanguage
🔴   37 |                 it[this.translationLanguage] = adventurePart.translationLanguage
🔴   38 |                 it[createdAt] = existingCreatedAt ?: now
🔴   39 |                 it[updatedAt] = now
⚪   40 |             }
⚪   41 | 
🔴   42 |             val latestMessageIndex = AdventureMessagesTable
🔴   43 |                 .select(AdventureMessagesTable.messageIndex)
🔴   44 |                 .where { AdventureMessagesTable.adventureId eq adventurePart.adventureId }
🔴   45 |                 .orderBy(AdventureMessagesTable.messageIndex, SortOrder.DESC)
🔴   46 |                 .limit(1)
🔴   47 |                 .singleOrNull()
🔴   48 |                 ?.get(AdventureMessagesTable.messageIndex)
⚪   49 | 
🔴   50 |             val messageIndex = (latestMessageIndex ?: -1) + 1
⚪   51 | 
🔴   52 |             AdventureMessagesTable.insert {
🔴   53 |                 it[this.adventureId] = adventurePart.adventureId
🔴   54 |                 it[this.sender] = SENDER_AI
🔴   55 |                 it[this.isEnding] = adventurePart.isEnding
🔴   56 |                 it[this.createdAt] = now
🔴   57 |                 it[this.messageIndex] = messageIndex
🔴   58 |             }
⚪   59 | 
🔴   60 |             AdventureSentencesTable.deleteWhere {
🔴   61 |                 (AdventureSentencesTable.adventureId eq adventurePart.adventureId) and
🔴   62 |                     (AdventureSentencesTable.messageIndex eq messageIndex)
⚪   63 |             }
🔴   64 |             adventurePart.paragraphs.forEachIndexed { paragraphIndex, paragraph ->
🔴   65 |                 paragraph.sentences.forEachIndexed { sentenceIndex, sentence ->
🔴   66 |                     AdventureSentencesTable.insert {
🔴   67 |                         it[this.adventureId] = adventurePart.adventureId
🔴   68 |                         it[this.messageIndex] = messageIndex
🔴   69 |                         it[this.paragraphIndex] = paragraphIndex
🔴   70 |                         it[this.sentenceIndex] = sentenceIndex
🔴   71 |                         it[language] = adventurePart.learningLanguage
🔴   72 |                         it[text] = sentence
🔴   73 |                     }
🔴   74 |                 }
🔴   75 |                 paragraph.translatedSentences.forEachIndexed { sentenceIndex, translatedSentence ->
🔴   76 |                     AdventureSentencesTable.insert {
🔴   77 |                         it[this.adventureId] = adventurePart.adventureId
🔴   78 |                         it[this.messageIndex] = messageIndex
🔴   79 |                         it[this.paragraphIndex] = paragraphIndex
🔴   80 |                         it[this.sentenceIndex] = sentenceIndex
🔴   81 |                         it[language] = adventurePart.translationLanguage
🔴   82 |                         it[text] = translatedSentence
🔴   83 |                     }
🔴   84 |                 }
🔴   85 |             }
⚪   86 |         }
⚪   87 |     }
⚪   88 | 
🔴   89 |     override fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse? = transaction(database) {
🔴   90 |         val adventureRow = AdventuresTable
🔴   91 |             .selectAll()
🔴   92 |             .where { AdventuresTable.id eq adventureId }
🔴   93 |             .singleOrNull()
🔴   94 |             ?: return@transaction null
⚪   95 | 
🔴   96 |         val sentenceRows = AdventureSentencesTable
🔴   97 |             .selectAll()
🔴   98 |             .where { AdventureSentencesTable.adventureId eq adventureId }
🔴   99 |             .orderBy(AdventureSentencesTable.messageIndex, SortOrder.ASC)
🔴  100 |             .orderBy(AdventureSentencesTable.paragraphIndex, SortOrder.ASC)
🔴  101 |             .orderBy(AdventureSentencesTable.sentenceIndex, SortOrder.ASC)
🔴  102 |             .toList()
⚪  103 | 
🔴  104 |         val sentencesByMessage = sentenceRows.groupBy { it[AdventureSentencesTable.messageIndex] }
🔴  105 |         val messages = AdventureMessagesTable
🔴  106 |             .selectAll()
🔴  107 |             .where { AdventureMessagesTable.adventureId eq adventureId }
🔴  108 |             .orderBy(AdventureMessagesTable.messageIndex, SortOrder.ASC)
🔴  109 |             .map { messageRow ->
🔴  110 |                 val messageIndex = messageRow[AdventureMessagesTable.messageIndex]
🔴  111 |                 val paragraphs = sentencesByMessage[messageIndex]
🔴  112 |                     .orEmpty()
🔴  113 |                     .groupBy { it[AdventureSentencesTable.paragraphIndex] }
🔴  114 |                     .toSortedMap()
🔴  115 |                     .values
🔴  116 |                     .map { paragraphRows ->
🔴  117 |                         val sourceSentences = paragraphRows
🔴  118 |                             .filter { it[AdventureSentencesTable.language] == adventureRow[AdventuresTable.learningLanguage] }
🔴  119 |                             .sortedBy { it[AdventureSentencesTable.sentenceIndex] }
🔴  120 |                             .map { it[AdventureSentencesTable.text] }
🔴  121 |                         val translatedSentences = paragraphRows
🔴  122 |                             .filter { it[AdventureSentencesTable.language] == adventureRow[AdventuresTable.translationLanguage] }
🔴  123 |                             .sortedBy { it[AdventureSentencesTable.sentenceIndex] }
🔴  124 |                             .map { it[AdventureSentencesTable.text] }
🔴  125 |                         TextAdventureParagraphRemoteResponse(
🔴  126 |                             sentences = sourceSentences,
🔴  127 |                             translatedSentences = translatedSentences,
🔴  128 |                         )
⚪  129 |                     }
⚪  130 | 
🔴  131 |                 TextAdventureMessageRemoteResponse(
🔴  132 |                     sender = messageRow[AdventureMessagesTable.sender],
🔴  133 |                     isEnding = messageRow[AdventureMessagesTable.isEnding],
🔴  134 |                     paragraphs = paragraphs,
🔴  135 |                 )
⚪  136 |             }
⚪  137 | 
🔴  138 |         TextAdventureMessagesRemoteResponse(
🔴  139 |             adventureId = adventureId,
🔴  140 |             title = adventureRow[AdventuresTable.title],
🔴  141 |             learningLanguage = adventureRow[AdventuresTable.learningLanguage],
🔴  142 |             translationsLanguage = adventureRow[AdventuresTable.translationLanguage],
🔴  143 |             messages = messages,
⚪  144 |         )
⚪  145 |     }
```

## Lines 150-200

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/DatabaseAdventureRepository.kt:150-200`

```kotlin
⚪  150 | }
⚪  151 | 
🔴  152 | object AdventuresTable : Table("text_adventure") {
🔴  153 |     val id = varchar("id", length = 255)
🔴  154 |     val title = text("title")
🔴  155 |     val learningLanguage = varchar("learning_language", length = 64)
🔴  156 |     val translationLanguage = varchar("translation_language", length = 64)
🔴  157 |     val createdAt = registerColumn("created_at", LongColumnType())
🔴  158 |     val updatedAt = registerColumn("updated_at", LongColumnType())
⚪  159 | 
🔴  160 |     override val primaryKey: PrimaryKey = PrimaryKey(id)
⚪  161 | }
⚪  162 | 
🔴  163 | object AdventureMessagesTable : Table("text_adventure_message") {
🔴  164 |     val adventureId = varchar("adventure_id", length = 255).references(
🔴  165 |         AdventuresTable.id,
🔴  166 |         onDelete = ReferenceOption.CASCADE,
⚪  167 |     )
🔴  168 |     val sender = varchar("sender", length = 32)
🔴  169 |     val isEnding = bool("is_ending")
🔴  170 |     val createdAt = registerColumn("created_at", LongColumnType())
🔴  171 |     val messageIndex = integer("message_index")
⚪  172 | 
🔴  173 |     override val primaryKey: PrimaryKey = PrimaryKey(adventureId, messageIndex)
⚪  174 | }
⚪  175 | 
🔴  176 | object AdventureSentencesTable : Table("text_adventure_sentence") {
🔴  177 |     val adventureId = varchar("adventure_id", length = 255)
🔴  178 |     val messageIndex = integer("message_index")
🔴  179 |     val paragraphIndex = integer("paragraph_index")
🔴  180 |     val sentenceIndex = integer("sentence_index")
🔴  181 |     val language = varchar("language", length = 64)
🔴  182 |     val text = text("text")
⚪  183 | 
🔴  184 |     init {
🔴  185 |         foreignKey(
🔴  186 |             adventureId,
🔴  187 |             messageIndex,
🔴  188 |             target = AdventureMessagesTable.primaryKey,
🔴  189 |             onDelete = ReferenceOption.CASCADE,
⚪  190 |         )
🔴  191 |     }
⚪  192 | 
🔴  193 |     override val primaryKey: PrimaryKey = PrimaryKey(
🔴  194 |         adventureId,
🔴  195 |         messageIndex,
🔴  196 |         paragraphIndex,
🔴  197 |         sentenceIndex,
🔴  198 |         language,
⚪  199 |     )
⚪  200 | }
```
