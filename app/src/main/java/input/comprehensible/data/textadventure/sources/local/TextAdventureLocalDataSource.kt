package input.comprehensible.data.textadventure.sources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ktin.InjectedSingleton
import input.comprehensible.data.textadventure.sources.local.model.TextAdventureEntity
import input.comprehensible.data.textadventure.sources.local.model.TextAdventureMessageEntity
import input.comprehensible.data.textadventure.sources.local.model.TextAdventureWithMessages
import kotlinx.coroutines.flow.Flow

@Dao
interface TextAdventureLocalDataSource {
    @Transaction
    @Query("""
        SELECT *
        FROM TextAdventureEntity
        WHERE id = :id
    """)
    fun observeAdventure(id: String): Flow<TextAdventureWithMessages?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdventure(adventure: TextAdventureEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<TextAdventureMessageEntity>)

    @Query("""
        SELECT MAX(sequenceIndex)
        FROM TextAdventureMessageEntity
        WHERE adventureId = :adventureId
    """)
    suspend fun getLatestMessageIndex(adventureId: String): Int?

    companion object : InjectedSingleton<TextAdventureLocalDataSource>()
}
