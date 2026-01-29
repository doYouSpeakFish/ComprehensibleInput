package input.comprehensible.data.textadventures.sources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ktin.InjectedSingleton
import kotlinx.coroutines.flow.Flow

@Suppress("TooManyFunctions")
@Dao
interface TextAdventuresLocalDataSource {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdventure(adventure: TextAdventureEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<TextAdventureMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentences(sentences: List<TextAdventureSentenceEntity>)

    @Query(
        """
            SELECT * FROM TextAdventureEntity
            ORDER BY updatedAt DESC
        """
    )
    fun getAdventures(): Flow<List<TextAdventureEntity>>

    @Query(
        """
            SELECT * FROM TextAdventureEntity
            WHERE id = :id
            LIMIT 1
        """
    )
    fun getAdventure(id: String): Flow<TextAdventureEntity?>

    @Query(
        """
            SELECT * FROM TextAdventureEntity
            WHERE id = :id
            LIMIT 1
        """
    )
    suspend fun getAdventureSnapshot(id: String): TextAdventureEntity?

    @Query(
        """
            SELECT * FROM TextAdventureMessageEntity
            WHERE adventureId = :adventureId
            ORDER BY messageIndex ASC
        """
    )
    fun getMessages(adventureId: String): Flow<List<TextAdventureMessageEntity>>

    @Query(
        """
            SELECT MAX(messageIndex)
            FROM TextAdventureMessageEntity
            WHERE adventureId = :adventureId
        """
    )
    suspend fun getLatestMessageIndex(adventureId: String): Int?

    @Query(
        """
            SELECT * FROM TextAdventureMessageEntity
            WHERE adventureId = :adventureId
            ORDER BY messageIndex ASC
        """
    )
    suspend fun getMessagesSnapshot(adventureId: String): List<TextAdventureMessageEntity>

    @Query(
        """
            SELECT * FROM TextAdventureMessageEntity
            ORDER BY messageIndex ASC
        """
    )
    fun getAllMessages(): Flow<List<TextAdventureMessageEntity>>

    @Query(
        """
            SELECT sentences.*
            FROM TextAdventureSentenceEntity AS sentences
            INNER JOIN TextAdventureMessageEntity AS messages
                ON messages.id = sentences.messageId
            WHERE messages.adventureId = :adventureId
            ORDER BY sentences.paragraphIndex ASC, sentences.sentenceIndex ASC
        """
    )
    fun getSentences(adventureId: String): Flow<List<TextAdventureSentenceEntity>>

    @Query(
        """
            SELECT sentences.*
            FROM TextAdventureSentenceEntity AS sentences
            INNER JOIN TextAdventureMessageEntity AS messages
                ON messages.id = sentences.messageId
            WHERE messages.adventureId = :adventureId
            ORDER BY sentences.paragraphIndex ASC, sentences.sentenceIndex ASC
        """
    )
    suspend fun getSentencesSnapshot(adventureId: String): List<TextAdventureSentenceEntity>

    @Query(
        """
            UPDATE TextAdventureEntity
            SET updatedAt = :updatedAt
            WHERE id = :id
        """
    )
    suspend fun updateAdventureUpdatedAt(
        id: String,
        updatedAt: Long,
    )

    companion object : InjectedSingleton<TextAdventuresLocalDataSource>()
}
