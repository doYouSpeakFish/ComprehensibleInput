package input.comprehensible.data.textadventure

import com.ktin.Singleton
import input.comprehensible.data.textadventure.model.TextAdventure
import input.comprehensible.data.textadventure.model.TextAdventureMessage
import input.comprehensible.data.textadventure.model.TextAdventureRole
import input.comprehensible.data.textadventure.sources.local.TextAdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.local.model.TextAdventureEntity
import input.comprehensible.data.textadventure.sources.local.model.TextAdventureMessageEntity
import input.comprehensible.data.textadventure.sources.local.model.TextAdventureWithMessages
import input.comprehensible.data.textadventure.sources.remote.TextAdventureRemoteDataSource
import input.comprehensible.data.textadventure.sources.remote.TextAdventureRemoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class TextAdventureRepository(
    private val localDataSource: TextAdventureLocalDataSource,
    private val remoteDataSource: TextAdventureRemoteDataSource,
) {
    fun adventure(adventureId: String): Flow<TextAdventure?> = localDataSource
        .observeAdventure(adventureId)
        .map { adventure -> adventure?.toModel() }

    suspend fun ensureAdventureStarted(
        adventureId: String,
        learningLanguage: String,
        translationLanguage: String,
    ) {
        val existingAdventure = localDataSource.observeAdventure(adventureId).first()
        if (existingAdventure?.messages?.isNotEmpty() == true) {
            return
        }
        val response = remoteDataSource.startAdventure(
            learningLanguage = learningLanguage,
            translationLanguage = translationLanguage,
        )
        localDataSource.insertAdventure(
            TextAdventureEntity(
                id = adventureId,
                isComplete = response.isEnding,
            )
        )
        localDataSource.insertMessages(
            listOf(
                response.toMessageEntity(
                    adventureId = adventureId,
                    sequenceIndex = 0,
                )
            )
        )
    }

    suspend fun submitResponse(
        adventureId: String,
        learningLanguage: String,
        translationLanguage: String,
        userResponse: String,
    ) {
        val nextIndex = (localDataSource.getLatestMessageIndex(adventureId) ?: -1) + 1
        val userMessage = TextAdventureMessageEntity(
            id = UUID.randomUUID().toString(),
            adventureId = adventureId,
            role = TextAdventureRole.USER,
            sentences = listOf(userResponse),
            translatedSentences = emptyList(),
            isEnding = false,
            sequenceIndex = nextIndex,
        )
        localDataSource.insertMessages(listOf(userMessage))

        val response = remoteDataSource.continueAdventure(
            adventureId = adventureId,
            learningLanguage = learningLanguage,
            translationLanguage = translationLanguage,
            userResponse = userResponse,
        )
        localDataSource.insertAdventure(
            TextAdventureEntity(
                id = adventureId,
                isComplete = response.isEnding,
            )
        )
        localDataSource.insertMessages(
            listOf(
                response.toMessageEntity(
                    adventureId = adventureId,
                    sequenceIndex = nextIndex + 1,
                )
            )
        )
    }

    private fun TextAdventureWithMessages.toModel() = TextAdventure(
        id = adventure.id,
        isComplete = adventure.isComplete,
        messages = messages
            .sortedBy { it.sequenceIndex }
            .map { it.toModel() },
    )

    private fun TextAdventureMessageEntity.toModel() = TextAdventureMessage(
        id = id,
        role = role,
        sentences = sentences,
        translatedSentences = translatedSentences,
        isEnding = isEnding,
    )

    private fun TextAdventureRemoteResponse.toMessageEntity(
        adventureId: String,
        sequenceIndex: Int,
    ) = TextAdventureMessageEntity(
        id = UUID.randomUUID().toString(),
        adventureId = adventureId,
        role = TextAdventureRole.AI,
        sentences = sentences,
        translatedSentences = translatedSentences,
        isEnding = isEnding,
        sequenceIndex = sequenceIndex,
    )

    companion object : Singleton<TextAdventureRepository>() {
        override fun create() = TextAdventureRepository(
            localDataSource = TextAdventureLocalDataSource(),
            remoteDataSource = TextAdventureRemoteDataSource(),
        )
    }
}
