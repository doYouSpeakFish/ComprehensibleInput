package input.comprehensible.data.textadventure.sources.local

import androidx.room.Dao
import androidx.room.Query
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

    companion object : InjectedSingleton<AdventureLocalDataSource>()
}
