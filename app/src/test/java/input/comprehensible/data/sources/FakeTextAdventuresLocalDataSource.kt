package input.comprehensible.data.sources

import input.comprehensible.data.textadventures.sources.local.TextAdventureEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageSentenceView
import input.comprehensible.data.textadventures.sources.local.TextAdventureSentenceEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureSummaryView
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

class FakeTextAdventuresLocalDataSource : TextAdventuresLocalDataSource {
    private val adventures = mutableMapOf<String, TextAdventureEntity>()
    private val messages = mutableListOf<TextAdventureMessageEntity>()
    private val sentences = mutableListOf<TextAdventureSentenceEntity>()

    var summariesFlow: Flow<List<TextAdventureSummaryView>> = flow { emit(emptyList()) }
    var sentenceRowsFlow: Flow<List<TextAdventureMessageSentenceView>> = flow { emit(emptyList()) }

    override suspend fun insertAdventure(adventure: TextAdventureEntity) {
        adventures[adventure.id] = adventure
    }

    override suspend fun insertMessages(messages: List<TextAdventureMessageEntity>) {
        this.messages.addAll(messages)
    }

    override suspend fun insertSentences(sentences: List<TextAdventureSentenceEntity>) {
        this.sentences.addAll(sentences)
    }

    override suspend fun insertMessageAndSentences(
        message: TextAdventureMessageEntity,
        sentences: List<TextAdventureSentenceEntity>,
    ) {
        messages.add(message)
        this.sentences.addAll(sentences)
    }

    override fun getAdventureSummaries(): Flow<List<TextAdventureSummaryView>> = summariesFlow

    override fun getAdventureSentenceRows(
        adventureId: String,
    ): Flow<List<TextAdventureMessageSentenceView>> = sentenceRowsFlow

    override suspend fun getAdventureSnapshot(id: String): TextAdventureEntity? = adventures[id]

    override suspend fun getLatestMessageIndex(adventureId: String): Int? =
        messages.filter { it.adventureId == adventureId }.maxOfOrNull { it.messageIndex }

    override suspend fun getMessagesSnapshot(adventureId: String): List<TextAdventureMessageEntity> =
        messages.filter { it.adventureId == adventureId }

    override suspend fun getSentencesSnapshot(adventureId: String): List<TextAdventureSentenceEntity> =
        sentences.filter { it.adventureId == adventureId }

    override suspend fun updateAdventureUpdatedAt(id: String, updatedAt: Long) {
        adventures[id]?.let { adventures[id] = it.copy(updatedAt = updatedAt) }
    }
}
