package input.comprehensible.data.textadventure.fakes

import input.comprehensible.data.textadventure.sources.local.AdventureEntity
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.local.MessageEntity
import input.comprehensible.data.textadventure.sources.local.MessageWithSentences
import input.comprehensible.data.textadventure.sources.local.SentenceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * In-memory fake of [AdventureLocalDataSource] backed by [MutableStateFlow]s so observed lists
 * update reactively, mirroring Room. Tests seed rows with [seed] and [seedMessage].
 */
class FakeAdventureLocalDataSource : AdventureLocalDataSource {
    private val rows = MutableStateFlow<List<AdventureEntity>>(emptyList())
    private val messageRows = MutableStateFlow<List<MessageEntity>>(emptyList())
    private val sentenceRows = MutableStateFlow<List<SentenceEntity>>(emptyList())
    private var nextSentenceId = 1L

    override fun observeAdventures(userId: String): Flow<List<AdventureEntity>> =
        rows.map { all ->
            all.filter { it.userId == userId }.sortedByDescending { it.updatedAt }
        }

    override suspend fun getAdventure(id: String): AdventureEntity? =
        rows.value.firstOrNull { it.id == id }

    override suspend fun upsertAdventures(adventures: List<AdventureEntity>) {
        adventures.forEach(::upsert)
    }

    override suspend fun upsertAdventure(adventure: AdventureEntity) {
        upsert(adventure)
    }

    override suspend fun deleteAdventure(id: String) {
        rows.value = rows.value.filterNot { it.id == id }
    }

    override fun observeMessages(adventureId: String): Flow<List<MessageWithSentences>> =
        combine(messageRows, sentenceRows) { messages, sentences ->
            messages
                .filter { it.adventureId == adventureId }
                .sortedBy { it.position }
                .map { message ->
                    MessageWithSentences(message, sentences.filter { it.messageId == message.id })
                }
        }

    override suspend fun upsertMessage(message: MessageEntity) {
        messageRows.value = messageRows.value.filterNot { it.id == message.id } + message
    }

    override suspend fun insertSentences(sentences: List<SentenceEntity>) {
        sentenceRows.value = sentenceRows.value + sentences.map { it.copy(id = nextSentenceId++) }
    }

    override suspend fun deleteMessages(adventureId: String) {
        val removedIds = messageRows.value.filter { it.adventureId == adventureId }.map { it.id }.toSet()
        messageRows.value = messageRows.value.filterNot { it.adventureId == adventureId }
        sentenceRows.value = sentenceRows.value.filterNot { it.messageId in removedIds }
    }

    fun seed(adventure: AdventureEntity) {
        upsert(adventure)
    }

    fun seedMessage(message: MessageEntity, sentences: List<SentenceEntity>) {
        messageRows.value = messageRows.value + message
        sentenceRows.value = sentenceRows.value + sentences.map { it.copy(id = nextSentenceId++) }
    }

    private fun upsert(adventure: AdventureEntity) {
        rows.value = rows.value.filterNot { it.id == adventure.id } + adventure
    }
}
