package input.comprehensible.data.textadventures

import com.ktin.Singleton
import input.comprehensible.data.textadventures.model.TextAdventure
import input.comprehensible.data.textadventures.model.TextAdventureMessage
import input.comprehensible.data.textadventures.model.TextAdventureMessageSender
import input.comprehensible.data.textadventures.model.TextAdventureSummary
import input.comprehensible.data.textadventures.sources.local.TextAdventureEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
        .map { adventures ->
            TextAdventuresListResult.Success(
                adventures.map { adventure -> adventure.toSummary() }
            ) as TextAdventuresListResult
        }
        .catch { throwable ->
            Timber.e(throwable, "Failed to load text adventures list")
            emit(TextAdventuresListResult.Error)
        }

    fun getAdventure(id: String): Flow<TextAdventureResult> = combine(
        localDataSource.getAdventure(id),
        localDataSource.getMessages(id),
    ) { adventure, messages ->
        if (adventure == null) {
            TextAdventureResult.Error
        } else {
            TextAdventureResult.Success(adventure.toDomain(messages))
        }
    }.catch { throwable ->
        Timber.e(throwable, "Failed to load text adventure %s", id)
        emit(TextAdventureResult.Error)
    }

    suspend fun startNewAdventure(
        learningLanguage: String,
        translationsLanguage: String,
    ): String {
        val adventureId = UUID.randomUUID().toString()
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
                isComplete = response.isEnding,
                createdAt = now,
                updatedAt = now,
            )
        )
        localDataSource.insertMessages(
            listOf(response.toMessageEntity(adventureId = adventureId, messageIndex = 0, createdAt = now))
        )
        return adventureId
    }

    suspend fun respondToAdventure(adventureId: String, userMessage: String) {
        val adventure = localDataSource.getAdventureSnapshot(adventureId) ?: run {
            Timber.e("Text adventure %s not found when responding", adventureId)
            return
        }
        val nextIndex = (localDataSource.getLatestMessageIndex(adventureId) ?: -1) + 1
        val now = clock()
        localDataSource.insertMessages(
            listOf(
                TextAdventureMessageEntity(
                    id = UUID.randomUUID().toString(),
                    adventureId = adventureId,
                    sender = TextAdventureMessageSender.USER,
                    sentences = listOf(userMessage),
                    translatedSentences = emptyList(),
                    isEnding = false,
                    createdAt = now,
                    messageIndex = nextIndex,
                )
            )
        )
        val response = remoteDataSource.respondToUser(
            adventureId = adventureId,
            learningLanguage = adventure.learningLanguage,
            translationsLanguage = adventure.translationLanguage,
            userMessage = userMessage,
        )
        localDataSource.insertMessages(
            listOf(
                response.toMessageEntity(
                    adventureId = adventureId,
                    messageIndex = nextIndex + 1,
                    createdAt = now,
                )
            )
        )
        localDataSource.updateAdventureCompletion(
            id = adventureId,
            isComplete = response.isEnding,
            updatedAt = now,
        )
    }

    private fun TextAdventureEntity.toSummary() = TextAdventureSummary(
        id = id,
        title = title,
        isComplete = isComplete,
        updatedAt = updatedAt,
    )

    private fun TextAdventureEntity.toDomain(
        messages: List<TextAdventureMessageEntity>,
    ) = TextAdventure(
        id = id,
        title = title,
        learningLanguage = learningLanguage,
        translationLanguage = translationLanguage,
        messages = messages
            .sortedBy { it.messageIndex }
            .map { it.toDomain() },
        isComplete = isComplete,
    )

    private fun TextAdventureMessageEntity.toDomain() = TextAdventureMessage(
        id = id,
        sender = sender,
        sentences = sentences,
        translatedSentences = translatedSentences,
        isEnding = isEnding,
    )

    private fun TextAdventureRemoteResponse.toMessageEntity(
        adventureId: String,
        messageIndex: Int,
        createdAt: Long,
    ) = TextAdventureMessageEntity(
        id = UUID.randomUUID().toString(),
        adventureId = adventureId,
        sender = TextAdventureMessageSender.AI,
        sentences = sentences,
        translatedSentences = translatedSentences,
        isEnding = isEnding,
        createdAt = createdAt,
        messageIndex = messageIndex,
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

sealed interface TextAdventureResult {
    data class Success(val adventure: TextAdventure) : TextAdventureResult
    object Error : TextAdventureResult
}
