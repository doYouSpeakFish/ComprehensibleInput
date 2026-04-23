package input.comprehensible.backend.textadventure

import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessagesRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureParagraphRemoteResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.ResultRow
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
            val existingCreatedAt = findAdventureCreatedAt(adventurePart.adventureId)
            upsertAdventure(adventurePart = adventurePart, now = now, existingCreatedAt = existingCreatedAt)

            val messageIndex = findNextMessageIndex(adventurePart.adventureId)
            insertMessage(adventurePart = adventurePart, messageIndex = messageIndex, now = now)
            replaceSentencesForMessage(adventurePart = adventurePart, messageIndex = messageIndex)
        }
    }

    override fun getAdventureMessages(adventureId: String): TextAdventureMessagesRemoteResponse? = transaction(database) {
        val adventureRow = findAdventureRow(adventureId) ?: return@transaction null
        val sentenceRowsByMessage = findSentenceRowsByMessage(adventureId)
        val messages = findMessageRows(adventureId).map { messageRow ->
            messageRow.toRemoteMessage(
                sentencesForMessage = sentenceRowsByMessage[messageRow[AdventureMessagesTable.messageIndex]].orEmpty(),
                learningLanguage = adventureRow[AdventuresTable.learningLanguage],
                translationLanguage = adventureRow[AdventuresTable.translationLanguage],
            )
        }

        adventureRow.toRemoteAdventureMessages(adventureId = adventureId, messages = messages)
    }

    private fun findAdventureCreatedAt(adventureId: String): Long? = AdventuresTable
        .select(AdventuresTable.createdAt)
        .where { AdventuresTable.id eq adventureId }
        .singleOrNull()
        ?.get(AdventuresTable.createdAt)

    private fun upsertAdventure(
        adventurePart: PersistedAdventurePart,
        now: Long,
        existingCreatedAt: Long?,
    ) {
        AdventuresTable.upsert {
            it[id] = adventurePart.adventureId
            it[this.title] = adventurePart.title
            it[this.learningLanguage] = adventurePart.learningLanguage
            it[this.translationLanguage] = adventurePart.translationLanguage
            it[createdAt] = existingCreatedAt ?: now
            it[updatedAt] = now
        }
    }

    private fun findNextMessageIndex(adventureId: String): Int {
        val latestMessageIndex = AdventureMessagesTable
            .select(AdventureMessagesTable.messageIndex)
            .where { AdventureMessagesTable.adventureId eq adventureId }
            .orderBy(AdventureMessagesTable.messageIndex, SortOrder.DESC)
            .limit(1)
            .singleOrNull()
            ?.get(AdventureMessagesTable.messageIndex)

        return (latestMessageIndex ?: -1) + 1
    }

    private fun insertMessage(adventurePart: PersistedAdventurePart, messageIndex: Int, now: Long) {
        AdventureMessagesTable.insert {
            it[this.adventureId] = adventurePart.adventureId
            it[this.sender] = senderAi
            it[this.isEnding] = adventurePart.isEnding
            it[this.createdAt] = now
            it[this.messageIndex] = messageIndex
        }
    }

    private fun replaceSentencesForMessage(adventurePart: PersistedAdventurePart, messageIndex: Int) {
        AdventureSentencesTable.deleteWhere {
            (AdventureSentencesTable.adventureId eq adventurePart.adventureId) and
                (AdventureSentencesTable.messageIndex eq messageIndex)
        }

        adventurePart.paragraphs.forEachIndexed { paragraphIndex, paragraph ->
            insertSentencesForMessage(
                adventureId = adventurePart.adventureId,
                messageIndex = messageIndex,
                paragraphIndex = paragraphIndex,
                language = adventurePart.learningLanguage,
                sentences = paragraph.sentences,
            )
            insertSentencesForMessage(
                adventureId = adventurePart.adventureId,
                messageIndex = messageIndex,
                paragraphIndex = paragraphIndex,
                language = adventurePart.translationLanguage,
                sentences = paragraph.translatedSentences,
            )
        }
    }

    private fun findAdventureRow(adventureId: String): ResultRow? = AdventuresTable
        .selectAll()
        .where { AdventuresTable.id eq adventureId }
        .singleOrNull()

    private fun findSentenceRowsByMessage(adventureId: String): Map<Int, List<ResultRow>> = AdventureSentencesTable
        .selectAll()
        .where { AdventureSentencesTable.adventureId eq adventureId }
        .orderBy(AdventureSentencesTable.messageIndex, SortOrder.ASC)
        .orderBy(AdventureSentencesTable.paragraphIndex, SortOrder.ASC)
        .orderBy(AdventureSentencesTable.sentenceIndex, SortOrder.ASC)
        .toList()
        .groupBy { it[AdventureSentencesTable.messageIndex] }

    private fun findMessageRows(adventureId: String): List<ResultRow> = AdventureMessagesTable
        .selectAll()
        .where { AdventureMessagesTable.adventureId eq adventureId }
        .orderBy(AdventureMessagesTable.messageIndex, SortOrder.ASC)
        .toList()

    private companion object {
        const val senderAi = "AI"
    }
}

private fun ResultRow.toRemoteMessage(
    sentencesForMessage: List<ResultRow>,
    learningLanguage: String,
    translationLanguage: String,
): TextAdventureMessageRemoteResponse {
    val paragraphs = sentencesForMessage
        .groupBy { it[AdventureSentencesTable.paragraphIndex] }
        .toSortedMap()
        .values
        .map { paragraphRows ->
            val sourceSentences = paragraphRows
                .filter { it[AdventureSentencesTable.language] == learningLanguage }
                .sortedBy { it[AdventureSentencesTable.sentenceIndex] }
                .map { it[AdventureSentencesTable.text] }
            val translatedSentences = paragraphRows
                .filter { it[AdventureSentencesTable.language] == translationLanguage }
                .sortedBy { it[AdventureSentencesTable.sentenceIndex] }
                .map { it[AdventureSentencesTable.text] }

            TextAdventureParagraphRemoteResponse(
                sentences = sourceSentences,
                translatedSentences = translatedSentences,
            )
        }

    return TextAdventureMessageRemoteResponse(
        sender = this[AdventureMessagesTable.sender],
        isEnding = this[AdventureMessagesTable.isEnding],
        paragraphs = paragraphs,
    )
}

private fun ResultRow.toRemoteAdventureMessages(
    adventureId: String,
    messages: List<TextAdventureMessageRemoteResponse>,
): TextAdventureMessagesRemoteResponse = TextAdventureMessagesRemoteResponse(
    adventureId = adventureId,
    title = this[AdventuresTable.title],
    learningLanguage = this[AdventuresTable.learningLanguage],
    translationsLanguage = this[AdventuresTable.translationLanguage],
    messages = messages,
)

private fun insertSentencesForMessage(
    adventureId: String,
    messageIndex: Int,
    paragraphIndex: Int,
    language: String,
    sentences: List<String>,
) {
    sentences.forEachIndexed { sentenceIndex, sentence ->
        AdventureSentencesTable.insert {
            it[this.adventureId] = adventureId
            it[this.messageIndex] = messageIndex
            it[this.paragraphIndex] = paragraphIndex
            it[this.sentenceIndex] = sentenceIndex
            it[this.language] = language
            it[this.text] = sentence
        }
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
