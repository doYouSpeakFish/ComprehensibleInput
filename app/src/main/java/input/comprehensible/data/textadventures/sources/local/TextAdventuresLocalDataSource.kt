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
    suspend fun insertMessages(messages: List<TextAdventureMessageEntity>)

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
            UPDATE TextAdventureEntity
            SET isComplete = :isComplete,
                updatedAt = :updatedAt
            WHERE id = :id
        """
    )
    suspend fun updateAdventureCompletion(
        id: String,
        isComplete: Boolean,
        updatedAt: Long,
    )

    companion object : InjectedSingleton<TextAdventuresLocalDataSource>()
}
