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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageAndSentences(
        message: TextAdventureMessageEntity,
        sentences: List<TextAdventureSentenceEntity>,
    )

    @Query(
        """
            SELECT * FROM TextAdventureSummaryView
            ORDER BY updatedAt DESC
        """
    )
    fun getAdventureSummaries(): Flow<List<TextAdventureSummaryView>>

    @Query(
        """
            SELECT * FROM TextAdventureMessageSentenceView
            WHERE adventureId = :adventureId
            ORDER BY messageIndex ASC, paragraphIndex ASC, sentenceIndex ASC
        """
    )
    fun getAdventureSentenceRows(adventureId: String): Flow<List<TextAdventureMessageSentenceView>>

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
            SELECT sentences.*
            FROM TextAdventureSentenceEntity AS sentences
            INNER JOIN TextAdventureMessageEntity AS messages
                ON messages.adventureId = sentences.adventureId
                AND messages.messageIndex = sentences.messageIndex
            WHERE sentences.adventureId = :adventureId
            ORDER BY sentences.messageIndex ASC, sentences.paragraphIndex ASC, sentences.sentenceIndex ASC
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
