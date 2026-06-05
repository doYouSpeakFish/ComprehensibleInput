package input.comprehensible.data.textadventure.sources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ktin.InjectedSingleton
import kotlinx.coroutines.flow.Flow

/**
 * Local store for cached [AdventureEntity] rows. Defined as an interface so it can be faked in
 * tests; the Room implementation is provided by the app's database.
 */
@Dao
interface AdventureLocalDataSource {
    @Query("SELECT * FROM adventure WHERE userId = :userId ORDER BY updatedAt DESC")
    fun observeAdventures(userId: String): Flow<List<AdventureEntity>>

    @Query("SELECT * FROM adventure WHERE id = :id")
    suspend fun getAdventure(id: String): AdventureEntity?

    @Upsert
    suspend fun upsertAdventures(adventures: List<AdventureEntity>)

    @Upsert
    suspend fun upsertAdventure(adventure: AdventureEntity)

    @Query("DELETE FROM adventure WHERE id = :id")
    suspend fun deleteAdventure(id: String)

    @Transaction
    @Query("SELECT * FROM message WHERE adventureId = :adventureId ORDER BY position")
    fun observeMessages(adventureId: String): Flow<List<MessageWithSentences>>

    @Upsert
    suspend fun upsertMessage(message: MessageEntity)

    @Insert
    suspend fun insertSentences(sentences: List<SentenceEntity>)

    @Query("DELETE FROM message WHERE adventureId = :adventureId")
    suspend fun deleteMessages(adventureId: String)

    companion object : InjectedSingleton<AdventureLocalDataSource>()
}
