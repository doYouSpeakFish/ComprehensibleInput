package input.comprehensible.backend.textadventure

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureParagraphRemoteResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

class DatabaseAdventureRepository(
    private val database: Database,
    private val nowProvider: () -> Long = { System.currentTimeMillis() },
) : AdventureRepository {
    override fun saveAdventurePart(adventurePart: PersistedAdventurePart) {
        transaction(database) {
            val now = nowProvider()
            val existingCreatedAt = AdventuresTable
                .select(AdventuresTable.createdAt)
                .where { AdventuresTable.id eq adventurePart.adventureId }
                .singleOrNull()
                ?.get(AdventuresTable.createdAt)

            AdventuresTable.upsert {
                it[id] = adventurePart.adventureId
                it[this.title] = adventurePart.title
                it[this.learningLanguage] = adventurePart.learningLanguage
                it[this.translationLanguage] = adventurePart.translationLanguage
                it[createdAt] = existingCreatedAt ?: now
                it[updatedAt] = now
            }

            val latestMessageIndex = AdventureMessagesTable
                .select(AdventureMessagesTable.messageIndex)
                .where { AdventureMessagesTable.adventureId eq adventurePart.adventureId }
                .orderBy(AdventureMessagesTable.messageIndex, SortOrder.DESC)
                .limit(1)
                .singleOrNull()
                ?.get(AdventureMessagesTable.messageIndex)

            val messageIndex = (latestMessageIndex ?: -1) + 1

            AdventureMessagesTable.insert {
                it[this.adventureId] = adventurePart.adventureId
                it[this.sender] = SENDER_AI
                it[this.isEnding] = adventurePart.isEnding
                it[this.createdAt] = now
                it[this.messageIndex] = messageIndex
            }

            AdventureSentencesTable.deleteWhere {
                (AdventureSentencesTable.adventureId eq adventurePart.adventureId) and
                    (AdventureSentencesTable.messageIndex eq messageIndex)
            }
            adventurePart.paragraphs.forEachIndexed { paragraphIndex, paragraph ->
                paragraph.sentences.forEachIndexed { sentenceIndex, sentence ->
                    AdventureSentencesTable.insert {
                        it[this.adventureId] = adventurePart.adventureId
                        it[this.messageIndex] = messageIndex
                        it[this.paragraphIndex] = paragraphIndex
                        it[this.sentenceIndex] = sentenceIndex
                        it[language] = adventurePart.learningLanguage
                        it[text] = sentence
                    }
                }
                paragraph.translatedSentences.forEachIndexed { sentenceIndex, translatedSentence ->
                    AdventureSentencesTable.insert {
                        it[this.adventureId] = adventurePart.adventureId
                        it[this.messageIndex] = messageIndex
                        it[this.paragraphIndex] = paragraphIndex
                        it[this.sentenceIndex] = sentenceIndex
                        it[language] = adventurePart.translationLanguage
                        it[text] = translatedSentence
                    }
                }
            }
        }
    }

    override fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse? = transaction(database) {
        val adventureRow = AdventuresTable
            .selectAll()
            .where { AdventuresTable.id eq adventureId }
            .singleOrNull()
            ?: return@transaction null

        val sentenceRows = AdventureSentencesTable
            .selectAll()
            .where { AdventureSentencesTable.adventureId eq adventureId }
            .orderBy(AdventureSentencesTable.messageIndex, SortOrder.ASC)
            .orderBy(AdventureSentencesTable.paragraphIndex, SortOrder.ASC)
            .orderBy(AdventureSentencesTable.sentenceIndex, SortOrder.ASC)
            .toList()

        val sentencesByMessage = sentenceRows.groupBy { it[AdventureSentencesTable.messageIndex] }
        val messages = AdventureMessagesTable
            .selectAll()
            .where { AdventureMessagesTable.adventureId eq adventureId }
            .orderBy(AdventureMessagesTable.messageIndex, SortOrder.ASC)
            .map { messageRow ->
                val messageIndex = messageRow[AdventureMessagesTable.messageIndex]
                val paragraphs = sentencesByMessage[messageIndex]
                    .orEmpty()
                    .groupBy { it[AdventureSentencesTable.paragraphIndex] }
                    .toSortedMap()
                    .values
                    .map { paragraphRows ->
                        val sourceSentences = paragraphRows
                            .filter { it[AdventureSentencesTable.language] == adventureRow[AdventuresTable.learningLanguage] }
                            .sortedBy { it[AdventureSentencesTable.sentenceIndex] }
                            .map { it[AdventureSentencesTable.text] }
                        val translatedSentences = paragraphRows
                            .filter { it[AdventureSentencesTable.language] == adventureRow[AdventuresTable.translationLanguage] }
                            .sortedBy { it[AdventureSentencesTable.sentenceIndex] }
                            .map { it[AdventureSentencesTable.text] }
                        TextAdventureParagraphRemoteResponse(
                            sentences = sourceSentences,
                            translatedSentences = translatedSentences,
                        )
                    }

                TextAdventureMessageRemoteResponse(
                    sender = messageRow[AdventureMessagesTable.sender],
                    isEnding = messageRow[AdventureMessagesTable.isEnding],
                    paragraphs = paragraphs,
                )
            }

        TextAdventureMessagesRemoteResponse(
            adventureId = adventureId,
            title = adventureRow[AdventuresTable.title],
            learningLanguage = adventureRow[AdventuresTable.learningLanguage],
            translationsLanguage = adventureRow[AdventuresTable.translationLanguage],
            messages = messages,
        )
    }

    private companion object {
        const val SENDER_AI = "AI"
    }
}

object AdventuresTable : Table("text_adventure") {
    val id = varchar("id", length = 255)
    val title = text("title")
    val learningLanguage = varchar("learning_language", length = 64)
    val translationLanguage = varchar("translation_language", length = 64)
    val createdAt = registerColumn("created_at", LongColumnType())
    val updatedAt = registerColumn("updated_at", LongColumnType())

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object AdventureMessagesTable : Table("text_adventure_message") {
    val adventureId = varchar("adventure_id", length = 255).references(
        AdventuresTable.id,
        onDelete = ReferenceOption.CASCADE,
    )
    val sender = varchar("sender", length = 32)
    val isEnding = bool("is_ending")
    val createdAt = registerColumn("created_at", LongColumnType())
    val messageIndex = integer("message_index")

    override val primaryKey: PrimaryKey = PrimaryKey(adventureId, messageIndex)
}

object AdventureSentencesTable : Table("text_adventure_sentence") {
    val adventureId = varchar("adventure_id", length = 255)
    val messageIndex = integer("message_index")
    val paragraphIndex = integer("paragraph_index")
    val sentenceIndex = integer("sentence_index")
    val language = varchar("language", length = 64)
    val text = text("text")

    init {
        foreignKey(
            adventureId,
            messageIndex,
            target = AdventureMessagesTable.primaryKey,
            onDelete = ReferenceOption.CASCADE,
        )
    }

    override val primaryKey: PrimaryKey = PrimaryKey(
        adventureId,
        messageIndex,
        paragraphIndex,
        sentenceIndex,
        language,
    )
}
