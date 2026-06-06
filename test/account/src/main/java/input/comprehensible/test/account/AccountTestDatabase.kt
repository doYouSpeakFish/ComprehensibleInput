package input.comprehensible.test.account

import androidx.room.Database
import androidx.room.RoomDatabase
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.user.UserEntity

/**
 * Room database used only by the account feature tests. It holds the single [UserEntity] table the
 * account module persists, so the tests can run against the real [UserLocalDataSource] backed by an
 * in-memory database instead of a hand-written fake.
 *
 * It lives in this module's main source set (rather than `:data:account`'s test fixtures) so Room's
 * KSP processor resolves the [UserEntity] that lives in `:common`, mirroring how `:app`'s `AppDb`
 * is set up.
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AccountTestDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserLocalDataSource
}
