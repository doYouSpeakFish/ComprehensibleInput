package input.comprehensible.data.textadventures.sources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Transaction
    suspend fun insertResponsePair(
        userMessage: TextAdventureMessageEntity,
        userSentences: List<TextAdventureSentenceEntity>,
        aiMessage: TextAdventureMessageEntity,
        aiSentences: List<TextAdventureSentenceEntity>,
    ) {
        insertMessageAndSentences(userMessage, userSentences)
        insertMessageAndSentences(aiMessage, aiSentences)
        updateAdventureUpdatedAt(id = userMessage.adventureId, updatedAt = aiMessage.createdAt)
    }

    @Query(
        """
            SELECT * FROM TextAdventureSummaryView
            WHERE userId = :userId
            ORDER BY updatedAt DESC
        """
    )
    fun getAdventureSummaries(userId: String): Flow<List<TextAdventureSummaryView>>

    @Query(
        """
            WITH RECURSIVE message_chain(id, depth) AS (
                SELECT id, 0
                FROM TextAdventureMessageEntity
                WHERE adventureId = :adventureId AND parentId IS NULL
                UNION ALL
                SELECT m.id, mc.depth + 1
                FROM TextAdventureMessageEntity m
                INNER JOIN message_chain mc ON m.parentId = mc.id
            )
            SELECT v.*
            FROM TextAdventureMessageSentenceView v
            INNER JOIN message_chain mc ON mc.id = v.messageId
            WHERE v.adventureId = :adventureId
            ORDER BY mc.depth ASC, v.paragraphIndex ASC, v.sentenceIndex ASC
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
            SELECT m.id FROM TextAdventureMessageEntity m
            WHERE m.adventureId = :adventureId
            AND NOT EXISTS (
                SELECT 1 FROM TextAdventureMessageEntity child
                WHERE child.adventureId = :adventureId AND child.parentId = m.id
            )
            ORDER BY m.createdAt DESC
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
