package input.comprehensible.data.account.sources.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ktin.InjectedSingleton
import input.comprehensible.data.user.UserEntity

/**
 * Persists the signed-in [UserEntity]. Defined as an interface so it can be faked in tests; the
 * Room implementation is provided by the app's database.
 */
@Dao
interface UserLocalDataSource {
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUser(id: String)

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUser(id: String): UserEntity?

    companion object : InjectedSingleton<UserLocalDataSource>()
}
