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
    suspend fun insertParagraphs(paragraphs: List<TextAdventureParagraphEntity>)

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
            SELECT * FROM TextAdventureParagraphEntity
            WHERE adventureId = :adventureId
            ORDER BY paragraphIndex ASC
        """
    )
    fun getParagraphs(adventureId: String): Flow<List<TextAdventureParagraphEntity>>

    @Query(
        """
            SELECT * FROM TextAdventureSentenceEntity
            WHERE adventureId = :adventureId
            ORDER BY sentenceIndex ASC
        """
    )
    fun getSentences(adventureId: String): Flow<List<TextAdventureSentenceEntity>>

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
