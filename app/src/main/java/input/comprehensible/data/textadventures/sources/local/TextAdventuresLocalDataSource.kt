package input.comprehensible.data.textadventures.sources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ktin.InjectedSingleton
import kotlinx.coroutines.flow.Flow

@Dao
interface TextAdventuresLocalDataSource {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdventure(adventure: TextAdventureEntity)

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
            ORDER BY createdAt ASC, paragraphIndex ASC, sentenceIndex ASC
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
            SELECT id FROM TextAdventureMessageEntity
            WHERE adventureId = :adventureId
            AND id NOT IN (
                SELECT parentId FROM TextAdventureMessageEntity
                WHERE adventureId = :adventureId AND parentId IS NOT NULL
            )
            LIMIT 1
        """
    )
    suspend fun getLeafMessageId(adventureId: String): String?

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
