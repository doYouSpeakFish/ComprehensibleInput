package input.comprehensible.data.textadventures

import com.ktin.Singleton
import input.comprehensible.data.account.AccountRepository
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
    private val accountRepository: AccountRepository,
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
        translationsLanguage: String,
    ): String {
        val token = getToken()
        val response = remoteDataSource.startAdventure(
            learningLanguage = learningLanguage,
            translationLanguage = translationsLanguage,
            token = token,
        )
        val now = clock()
        val adventureId = response.adventureId
        localDataSource.insertAdventure(
            TextAdventureEntity(
                id = adventureId,
                title = response.title,
                learningLanguage = learningLanguage,
                translationLanguage = translationsLanguage,
                createdAt = now,
                updatedAt = now,
            )
        )
        insertStartResponse(
            adventureId = adventureId,
            response = response,
            learningLanguage = learningLanguage,
            translationLanguage = translationsLanguage,
            createdAt = now,
        )
        return adventureId
    }

    suspend fun respondToAdventure(adventureId: String, userMessage: String) {
        val adventure = localDataSource.getAdventureSnapshot(adventureId) ?: run {
            Timber.e("Text adventure %s not found when responding", adventureId)
            return
        }
        val token = getToken()
        val leafMessageId = localDataSource.getLeafMessageId(adventureId) ?: run {
            Timber.e("No leaf message found for adventure %s", adventureId)
            return
        }

        val userMessageResponse = remoteDataSource.postUserMessage(
            adventureId = adventureId,
            parentId = leafMessageId,
            text = userMessage,
            token = token,
        )
        localDataSource.insertMessageAndSentences(
            userMessageResponse.toMessageEntity(adventureId = adventureId, createdAt = clock()),
            userMessageResponse.toSentenceEntities(
                learningLanguage = adventure.learningLanguage,
                translationLanguage = adventure.translationLanguage,
            ),
        )

        val aiMessageResponse = remoteDataSource.postAiMessage(
            adventureId = adventureId,
            parentId = userMessageResponse.id,
            token = token,
        )
        val now = clock()
        localDataSource.insertMessageAndSentences(
            aiMessageResponse.toMessageEntity(adventureId = adventureId, createdAt = now),
            aiMessageResponse.toSentenceEntities(
                learningLanguage = adventure.learningLanguage,
                translationLanguage = adventure.translationLanguage,
            ),
        )
        localDataSource.updateAdventureUpdatedAt(id = adventureId, updatedAt = now)
    }

    private suspend fun getToken(): String =
        accountRepository.session.first()?.token ?: error("User not authenticated")

    private suspend fun insertStartResponse(
        adventureId: String,
        response: TextAdventureRemoteResponse,
        learningLanguage: String,
        translationLanguage: String,
        createdAt: Long,
    ) {
        val messageEntity = TextAdventureMessageEntity(
            id = response.messageId,
            adventureId = adventureId,
            parentId = null,
            sender = TextAdventureMessageSender.AI,
            isEnding = response.isEnding,
            createdAt = createdAt,
        )
        val sentenceEntities = buildList {
            response.sentences.forEachIndexed { index, sentence ->
                add(
                    TextAdventureSentenceEntity(
                        messageId = response.messageId,
                        paragraphIndex = 0,
                        sentenceIndex = index,
                        language = learningLanguage,
                        text = sentence,
                    )
                )
            }
            response.translatedSentences.forEachIndexed { index, sentence ->
                add(
                    TextAdventureSentenceEntity(
                        messageId = response.messageId,
                        paragraphIndex = 0,
                        sentenceIndex = index,
                        language = translationLanguage,
                        text = sentence,
                    )
                )
            }
        }
        localDataSource.insertMessageAndSentences(messageEntity, sentenceEntities)
    }

    companion object : Singleton<TextAdventuresRepository>() {
        override fun create() = TextAdventuresRepository(
            localDataSource = TextAdventuresLocalDataSource(),
            remoteDataSource = TextAdventureRemoteDataSource(),
            accountRepository = AccountRepository(),
        )
    }
}

sealed interface TextAdventuresListResult {
    data class Success(val adventures: List<TextAdventureSummary>) : TextAdventuresListResult
    object Error : TextAdventuresListResult
}

sealed interface TextAdventureResult {
    data class Success(val adventure: TextAdventure) : TextAdventureResult
    object Error : TextAdventureResult
}

private fun TextAdventureSummaryView.toSummary() = TextAdventureSummary(
    id = adventureId,
    title = title,
    isComplete = isComplete,
    updatedAt = updatedAt,
)

private fun List<TextAdventureMessageSentenceView>.toDomain(): TextAdventure {
    val adventure = first()
    // groupBy preserves insertion order (LinkedHashMap); rows arrive ordered by parent-chain
    // depth from the recursive CTE query, so entries() is already in conversation order.
    val messageGroups = groupBy { it.messageId }
    val messageUi = messageGroups.entries.map { (messageId, messageRows) ->
            val paragraphs = messageRows
                .groupBy { it.paragraphIndex }
                .toSortedMap()
                .map { (paragraphIndex, paragraphRows) ->
                    paragraphRows.toDomain(
                        messageId = messageId,
                        paragraphIndex = paragraphIndex,
                        learningLanguage = adventure.learningLanguage,
                        translationLanguage = adventure.translationLanguage,
                    )
                }
            messageRows.first().toDomainMessage(paragraphs = paragraphs)
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

private fun TextAdventureMessageSentenceView.toDomainMessage(
    paragraphs: List<TextAdventureParagraph>,
) = TextAdventureMessage(
    id = messageId,
    sender = sender,
    paragraphs = paragraphs,
    isEnding = isEnding,
)

private fun List<TextAdventureMessageSentenceView>.toDomain(
    messageId: String,
    paragraphIndex: Int,
    learningLanguage: String,
    translationLanguage: String,
): TextAdventureParagraph {
    val sentencesByLanguage = sortedBy { it.sentenceIndex }.groupBy { it.language }
    return TextAdventureParagraph(
        id = "$messageId-$paragraphIndex",
        sentences = sentencesByLanguage[learningLanguage].orEmpty().map { it.text },
        translatedSentences = sentencesByLanguage[translationLanguage].orEmpty().map { it.text },
    )
}

private fun TextAdventureMessageRemoteResponse.toMessageEntity(
    adventureId: String,
    createdAt: Long,
) = TextAdventureMessageEntity(
    id = id,
    adventureId = adventureId,
    parentId = parentId,
    sender = if (type == "AI") TextAdventureMessageSender.AI else TextAdventureMessageSender.USER,
    isEnding = isEnding,
    createdAt = createdAt,
)

private fun TextAdventureMessageRemoteResponse.toSentenceEntities(
    learningLanguage: String,
    translationLanguage: String,
): List<TextAdventureSentenceEntity> = buildList {
    paragraphs.forEachIndexed { paragraphIndex, paragraph ->
        paragraph.sentences.forEachIndexed { sentenceIndex, sentence ->
            add(
                TextAdventureSentenceEntity(
                    messageId = id,
                    paragraphIndex = paragraphIndex,
                    sentenceIndex = sentenceIndex,
                    language = learningLanguage,
                    text = sentence,
                )
            )
        }
        paragraph.translatedSentences.forEachIndexed { sentenceIndex, sentence ->
            add(
                TextAdventureSentenceEntity(
                    messageId = id,
                    paragraphIndex = paragraphIndex,
                    sentenceIndex = sentenceIndex,
                    language = translationLanguage,
                    text = sentence,
                )
            )
        }
    }
}
