package input.comprehensible.data.textadventures

import com.ktin.Singleton
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.textadventures.model.TextAdventure
import input.comprehensible.data.textadventures.model.TextAdventureMessage
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender
import input.comprehensible.data.textadventures.model.TextAdventureParagraph
import input.comprehensible.data.textadventures.model.TextAdventureSummary
import input.comprehensible.data.textadventures.sources.local.TextAdventureEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageSentenceView
import input.comprehensible.data.textadventures.sources.local.TextAdventureSentenceEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureSummaryView
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureMessageRemoteResponse
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

class TextAdventuresRepository(
    private val localDataSource: TextAdventuresLocalDataSource,
    private val remoteDataSource: TextAdventureRemoteDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    fun getAdventures(): Flow<TextAdventuresListResult> = localDataSource
        .getAdventureSummaries()
        .map { summaries ->
            TextAdventuresListResult.Success(
                summaries.map { summary -> summary.toSummary() }
            ) as TextAdventuresListResult
        }
        .catch { throwable ->
            Timber.e(throwable, "Failed to load text adventures list")
            emit(TextAdventuresListResult.Error)
        }

    fun getAdventure(id: String): Flow<TextAdventureResult> = localDataSource
        .getAdventureSentenceRows(id)
        .map { rows ->
            if (rows.isEmpty()) {
                TextAdventureResult.Error
            } else {
                TextAdventureResult.Success(rows.toDomain())
            }
        }
        .distinctUntilChanged()
        .catch { throwable ->
            Timber.e(throwable, "Failed to load text adventure %s", id)
            emit(TextAdventureResult.Error)
        }

    suspend fun startNewAdventure(
        learningLanguage: String,
        translationLanguage: String,
    ): String {
        val sessionToken = requireSessionToken()
        val response = remoteDataSource.startAdventure(
                learningLanguage = learningLanguage,
                translationLanguage = translationLanguage,
                sessionToken = sessionToken,
            )
        val now = clock()
        val adventureId = response.adventureId
        localDataSource.insertAdventure(
            TextAdventureEntity(
                id = adventureId,
                title = response.title,
                learningLanguage = learningLanguage,
                translationLanguage = translationLanguage,
                createdAt = now,
                updatedAt = now,
            )
        )
        insertAiResponseFromStart(
            adventureId = adventureId,
            response = response,
            learningLanguage = learningLanguage,
            translationLanguage = translationLanguage,
            messageIndex = 0,
            createdAt = now,
        )
        return adventureId
    }

    suspend fun respondToAdventure(adventureId: String, userMessage: String) {
        val adventure = localDataSource.getAdventureSnapshot(adventureId) ?: run {
            Timber.e("Text adventure %s not found when responding", adventureId)
            return
        }
        val parentMessageId = localDataSource.getLatestMessageId(adventureId) ?: run {
            Timber.e("No messages found for adventure %s", adventureId)
            return
        }
        val sessionToken = requireSessionToken()
        val nextIndex = (localDataSource.getLatestMessageIndex(adventureId) ?: -1) + 1
        val now = clock()

        val userMessageResponse = remoteDataSource.createUserMessage(
            adventureId = adventureId,
            parentMessageId = parentMessageId,
            text = userMessage,
            sessionToken = sessionToken,
        )
        insertMessageFromResponse(
            adventureId = adventureId,
            response = userMessageResponse,
            learningLanguage = adventure.learningLanguage,
            translationLanguage = adventure.translationLanguage,
            messageIndex = nextIndex,
            createdAt = now,
        )

        val aiResponse = remoteDataSource.createAiMessage(
            adventureId = adventureId,
            parentMessageId = userMessageResponse.id,
            sessionToken = sessionToken,
        )
        insertMessageFromResponse(
            adventureId = adventureId,
            response = aiResponse,
            learningLanguage = adventure.learningLanguage,
            translationLanguage = adventure.translationLanguage,
            messageIndex = nextIndex + 1,
            createdAt = now,
        )
        localDataSource.updateAdventureUpdatedAt(id = adventureId, updatedAt = now)
    }

    private suspend fun requireSessionToken(): String =
        accountLocalDataSource.session.first()?.token
            ?: error("No active session for text adventure request")

    private suspend fun insertAiResponseFromStart(
        adventureId: String,
        response: TextAdventureRemoteResponse,
        learningLanguage: String,
        translationLanguage: String,
        messageIndex: Int,
        createdAt: Long,
    ) {
        val messageEntity = TextAdventureMessageEntity(
            adventureId = adventureId,
            sender = TextAdventureMessageSender.AI,
            isEnding = response.isEnding,
            createdAt = createdAt,
            messageIndex = messageIndex,
            messageId = response.messageId,
        )
        val sentenceEntities = buildSentenceEntities(
            adventureId = adventureId,
            messageIndex = messageIndex,
            paragraphs = listOf(
                ParagraphContent(
                    sentences = response.sentences,
                    translatedSentences = response.translatedSentences,
                )
            ),
            learningLanguage = learningLanguage,
            translationLanguage = translationLanguage,
        )
        localDataSource.insertMessageAndSentences(messageEntity, sentenceEntities)
    }

    private suspend fun insertMessageFromResponse(
        adventureId: String,
        response: TextAdventureMessageRemoteResponse,
        learningLanguage: String,
        translationLanguage: String,
        messageIndex: Int,
        createdAt: Long,
    ) {
        val sender = when (response.type) {
            "AI" -> TextAdventureMessageSender.AI
            else -> TextAdventureMessageSender.USER
        }
        val messageEntity = TextAdventureMessageEntity(
            adventureId = adventureId,
            sender = sender,
            isEnding = response.isEnding,
            createdAt = createdAt,
            messageIndex = messageIndex,
            messageId = response.id,
        )
        val sentenceEntities = buildSentenceEntities(
            adventureId = adventureId,
            messageIndex = messageIndex,
            paragraphs = response.paragraphs.map { p ->
                ParagraphContent(sentences = p.sentences, translatedSentences = p.translatedSentences)
            },
            learningLanguage = learningLanguage,
            translationLanguage = translationLanguage,
        )
        localDataSource.insertMessageAndSentences(messageEntity, sentenceEntities)
    }

    private data class ParagraphContent(
        val sentences: List<String>,
        val translatedSentences: List<String>,
    )

    private fun buildSentenceEntities(
        adventureId: String,
        messageIndex: Int,
        paragraphs: List<ParagraphContent>,
        learningLanguage: String,
        translationLanguage: String,
    ): List<TextAdventureSentenceEntity> = buildList {
        paragraphs.forEachIndexed { paragraphIndex, paragraph ->
            paragraph.sentences.forEachIndexed { sentenceIndex, sentence ->
                add(
                    TextAdventureSentenceEntity(
                        adventureId = adventureId,
                        messageIndex = messageIndex,
                        paragraphIndex = paragraphIndex,
                        language = learningLanguage,
                        sentenceIndex = sentenceIndex,
                        text = sentence,
                    )
                )
            }
            paragraph.translatedSentences.forEachIndexed { sentenceIndex, sentence ->
                add(
                    TextAdventureSentenceEntity(
                        adventureId = adventureId,
                        messageIndex = messageIndex,
                        paragraphIndex = paragraphIndex,
                        language = translationLanguage,
                        sentenceIndex = sentenceIndex,
                        text = sentence,
                    )
                )
            }
        }
    }

    companion object : Singleton<TextAdventuresRepository>() {
        override fun create() = TextAdventuresRepository(
            localDataSource = TextAdventuresLocalDataSource(),
            remoteDataSource = TextAdventureRemoteDataSource(),
            accountLocalDataSource = AccountLocalDataSource(),
        )
    }
}

sealed interface TextAdventuresListResult {
    data class Success(val adventures: List<TextAdventureSummary>) : TextAdventuresListResult
    object Error : TextAdventuresListResult
}

private fun TextAdventureSummaryView.toSummary() = TextAdventureSummary(
    id = adventureId,
    title = title,
    isComplete = isComplete,
    updatedAt = updatedAt,
)

private fun List<TextAdventureMessageSentenceView>.toDomain(): TextAdventure {
    val adventure = first()
    val messageGroups = groupBy { it.messageIndex }
    val messageUi = messageGroups.keys.sorted().map { messageIndex ->
        val messageRows = messageGroups.getValue(messageIndex)
        val paragraphs = messageRows
            .groupBy { it.paragraphIndex }
            .toSortedMap()
            .map { (paragraphIndex, paragraphRows) ->
                paragraphRows.toDomain(
                    messageIndex = messageIndex,
                    paragraphIndex = paragraphIndex,
                    learningLanguage = adventure.learningLanguage,
                    translationLanguage = adventure.translationLanguage,
                )
            }
        messageRows.first().toMessageEntity().toDomain(paragraphs = paragraphs)
    }
    return TextAdventure(
        id = adventure.adventureId,
        title = adventure.title,
        learningLanguage = adventure.learningLanguage,
        translationLanguage = adventure.translationLanguage,
        messages = messageUi,
        isComplete = messageUi.lastOrNull()?.isEnding == true,
    )
}

private fun TextAdventureMessageEntity.toDomain(
    paragraphs: List<TextAdventureParagraph>,
) = TextAdventureMessage(
    id = "${adventureId}-${messageIndex}",
    sender = sender,
    paragraphs = paragraphs,
    isEnding = isEnding,
)

private fun List<TextAdventureMessageSentenceView>.toDomain(
    messageIndex: Int,
    paragraphIndex: Int,
    learningLanguage: String,
    translationLanguage: String,
): TextAdventureParagraph {
    val sentencesByLanguage = sortedBy { it.sentenceIndex }
        .groupBy { it.language }
    val adventureId = firstOrNull()?.adventureId.orEmpty()
    return TextAdventureParagraph(
        id = "${adventureId}-${messageIndex}-$paragraphIndex",
        sentences = sentencesByLanguage[learningLanguage].orEmpty().map { it.text },
        translatedSentences = sentencesByLanguage[translationLanguage].orEmpty().map { it.text },
    )
}

private fun TextAdventureMessageSentenceView.toMessageEntity() = TextAdventureMessageEntity(
    adventureId = adventureId,
    sender = sender,
    isEnding = isEnding,
    createdAt = 0L,
    messageIndex = messageIndex,
    messageId = "",
)

sealed interface TextAdventureResult {
    data class Success(val adventure: TextAdventure) : TextAdventureResult
    object Error : TextAdventureResult
}
