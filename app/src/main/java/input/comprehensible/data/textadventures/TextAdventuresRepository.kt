package input.comprehensible.data.textadventures

import com.ktin.Singleton
import input.comprehensible.data.textadventures.model.TextAdventure
import input.comprehensible.data.textadventures.model.TextAdventureMessage
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender
import input.comprehensible.data.textadventures.model.TextAdventureParagraph
import input.comprehensible.data.textadventures.model.TextAdventureSummary
import input.comprehensible.data.textadventures.sources.local.TextAdventureEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureSentenceEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureHistoryMessage
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID

class TextAdventuresRepository(
    private val localDataSource: TextAdventuresLocalDataSource,
    private val remoteDataSource: TextAdventureRemoteDataSource,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    fun getAdventures(): Flow<TextAdventuresListResult> = localDataSource
        .getAdventures()
        .combine(localDataSource.getAllMessages()) { adventures, messages ->
            val completionByAdventure = messages
                .groupBy { it.adventureId }
                .mapValues { (_, adventureMessages) ->
                    adventureMessages.maxByOrNull { it.messageIndex }?.isEnding == true
                }
            TextAdventuresListResult.Success(
                adventures.map { adventure ->
                    adventure.toSummary(isComplete = completionByAdventure[adventure.id] == true)
                }
            ) as TextAdventuresListResult
        }
        .catch { throwable ->
            Timber.e(throwable, "Failed to load text adventures list")
            emit(TextAdventuresListResult.Error)
        }

    fun getAdventure(id: String): Flow<TextAdventureResult> = combine(
        localDataSource.getAdventure(id),
        localDataSource.getMessages(id),
        localDataSource.getSentences(id),
    ) { adventure, messages, sentences ->
        if (adventure == null) {
            TextAdventureResult.Error
        } else {
            TextAdventureResult.Success(
                adventure.toDomain(
                    messages = messages,
                    sentences = sentences,
                )
            )
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
        val adventureId = UUID.randomUUID().toString()
        val languages = TextAdventureLanguages(
            learningLanguage = learningLanguage,
            translationLanguage = translationsLanguage,
        )
        val response = remoteDataSource.startAdventure(
            adventureId = adventureId,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )
        val now = clock()
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
        insertAiResponse(
            adventureId = adventureId,
            response = response,
            languages = languages,
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
        val languages = TextAdventureLanguages(
            learningLanguage = adventure.learningLanguage,
            translationLanguage = adventure.translationLanguage,
        )
        val nextIndex = (localDataSource.getLatestMessageIndex(adventureId) ?: -1) + 1
        val now = clock()
        insertUserMessage(
            adventureId = adventureId,
            message = userMessage,
            language = adventure.learningLanguage,
            messageIndex = nextIndex,
            createdAt = now,
        )
        val response = remoteDataSource.respondToUser(
            adventureId = adventureId,
            learningLanguage = adventure.learningLanguage,
            translationsLanguage = adventure.translationLanguage,
            userMessage = userMessage,
            history = buildHistory(
                localDataSource = localDataSource,
                adventureId = adventureId,
                learningLanguage = adventure.learningLanguage,
            ),
        )
        insertAiResponse(
            adventureId = adventureId,
            response = response,
            languages = languages,
            messageIndex = nextIndex + 1,
            createdAt = now,
        )
        localDataSource.updateAdventureUpdatedAt(
            id = adventureId,
            updatedAt = now,
        )
    }

    private fun TextAdventureEntity.toSummary(isComplete: Boolean) = TextAdventureSummary(
        id = id,
        title = title,
        isComplete = isComplete,
        updatedAt = updatedAt,
    )

    private fun TextAdventureEntity.toDomain(
        messages: List<TextAdventureMessageEntity>,
        sentences: List<TextAdventureSentenceEntity>,
    ): TextAdventure {
        val sentencesByMessage = sentences.groupBy { it.messageId }
        val messageUi = messages.sortedBy { it.messageIndex }.map { message ->
            val messageSentences = sentencesByMessage[message.id].orEmpty()
            val paragraphs = messageSentences
                .groupBy { it.paragraphIndex }
                .toSortedMap()
                .map { (paragraphIndex, paragraphSentences) ->
                    paragraphSentences.toDomain(
                        messageId = message.id,
                        paragraphIndex = paragraphIndex,
                        learningLanguage = learningLanguage,
                        translationLanguage = translationLanguage,
                    )
                }
            message.toDomain(paragraphs = paragraphs)
        }
        return TextAdventure(
            id = id,
            title = title,
            learningLanguage = learningLanguage,
            translationLanguage = translationLanguage,
            messages = messageUi,
            isComplete = messageUi.lastOrNull()?.isEnding == true,
        )
    }

    private fun TextAdventureMessageEntity.toDomain(
        paragraphs: List<TextAdventureParagraph>,
    ) = TextAdventureMessage(
        id = id,
        sender = sender,
        paragraphs = paragraphs,
        isEnding = isEnding,
    )

    private fun List<TextAdventureSentenceEntity>.toDomain(
        messageId: String,
        paragraphIndex: Int,
        learningLanguage: String,
        translationLanguage: String,
    ): TextAdventureParagraph {
        val sentencesByLanguage = sortedBy { it.sentenceIndex }
            .groupBy { it.language }
        return TextAdventureParagraph(
            id = "$messageId-$paragraphIndex",
            sentences = sentencesByLanguage[learningLanguage].orEmpty().map { it.text },
            translatedSentences = sentencesByLanguage[translationLanguage].orEmpty().map { it.text },
        )
    }

    private suspend fun insertUserMessage(
        adventureId: String,
        message: String,
        language: String,
        messageIndex: Int,
        createdAt: Long,
    ) {
        val messageId = UUID.randomUUID().toString()
        localDataSource.insertMessages(
            listOf(
                TextAdventureMessageEntity(
                    id = messageId,
                    adventureId = adventureId,
                    sender = TextAdventureMessageSender.USER,
                    isEnding = false,
                    createdAt = createdAt,
                    messageIndex = messageIndex,
                )
            )
        )
        localDataSource.insertSentences(
            listOf(
                TextAdventureSentenceEntity(
                    messageId = messageId,
                    paragraphIndex = 0,
                    language = language,
                    sentenceIndex = 0,
                    text = message,
                )
            )
        )
    }

    private suspend fun insertAiResponse(
        adventureId: String,
        response: TextAdventureRemoteResponse,
        languages: TextAdventureLanguages,
        messageIndex: Int,
        createdAt: Long,
    ) {
        val messageId = UUID.randomUUID().toString()
        localDataSource.insertMessages(
            listOf(
                TextAdventureMessageEntity(
                    id = messageId,
                    adventureId = adventureId,
                    sender = TextAdventureMessageSender.AI,
                    isEnding = response.isEnding,
                    createdAt = createdAt,
                    messageIndex = messageIndex,
                )
            )
        )
        val sentenceEntities = buildList {
            response.sentences.forEachIndexed { index, sentence ->
                add(
                    TextAdventureSentenceEntity(
                        messageId = messageId,
                        paragraphIndex = 0,
                        language = languages.learningLanguage,
                        sentenceIndex = index,
                        text = sentence,
                    )
                )
            }
            response.translatedSentences.forEachIndexed { index, sentence ->
                add(
                    TextAdventureSentenceEntity(
                        messageId = messageId,
                        paragraphIndex = 0,
                        language = languages.translationLanguage,
                        sentenceIndex = index,
                        text = sentence,
                    )
                )
            }
        }
        localDataSource.insertSentences(sentenceEntities)
    }

    private data class TextAdventureLanguages(
        val learningLanguage: String,
        val translationLanguage: String,
    )

    companion object : Singleton<TextAdventuresRepository>() {
        override fun create() = TextAdventuresRepository(
            localDataSource = TextAdventuresLocalDataSource(),
            remoteDataSource = TextAdventureRemoteDataSource(),
        )
    }
}

sealed interface TextAdventuresListResult {
    data class Success(val adventures: List<TextAdventureSummary>) : TextAdventuresListResult
    object Error : TextAdventuresListResult
}

private suspend fun buildHistory(
    localDataSource: TextAdventuresLocalDataSource,
    adventureId: String,
    learningLanguage: String,
): List<TextAdventureHistoryMessage> {
    val messages = localDataSource.getMessagesSnapshot(adventureId)
        .sortedBy { it.messageIndex }
    val sentencesByMessage = localDataSource.getSentencesSnapshot(adventureId)
        .filter { it.language == learningLanguage }
        .groupBy { it.messageId }
    return messages.mapNotNull { message ->
        val text = sentencesByMessage[message.id]
            .orEmpty()
            .groupBy { it.paragraphIndex }
            .toSortedMap()
            .mapNotNull { (_, paragraphSentences) ->
                val sentenceText = paragraphSentences
                    .sortedBy { it.sentenceIndex }
                    .joinToString(" ") { it.text.trim() }
                    .trim()
                sentenceText.takeIf { it.isNotBlank() }
            }
            .joinToString("\n\n")
            .trim()
        text.takeIf { it.isNotBlank() }?.let {
            TextAdventureHistoryMessage(
                role = when (message.sender) {
                    TextAdventureMessageSender.USER -> "user"
                    TextAdventureMessageSender.AI -> "assistant"
                },
                text = it,
            )
        }
    }
}

sealed interface TextAdventureResult {
    data class Success(val adventure: TextAdventure) : TextAdventureResult
    object Error : TextAdventureResult
}
