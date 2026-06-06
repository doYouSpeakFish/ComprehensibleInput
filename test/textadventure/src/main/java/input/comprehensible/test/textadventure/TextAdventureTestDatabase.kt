package input.comprehensible.test.textadventure

import androidx.room.Database
import androidx.room.RoomDatabase
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.textadventure.sources.local.AdventureEntity
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.local.MessageEntity
import input.comprehensible.data.textadventure.sources.local.SentenceEntity
import input.comprehensible.data.user.UserEntity

/**
 * Room database used only by the text adventure feature tests. It mirrors the slice of the app's
 * `AppDb` that the feature exercises (the [AdventureEntity] / [MessageEntity] / [SentenceEntity]
 * tables, scoped to a [UserEntity] through a foreign key) so the tests can run against the real
 * [AdventureLocalDataSource] backed by an in-memory database instead of a hand-written fake.
 *
 * It lives in this module's main source set (rather than `:data:textadventure`'s test fixtures)
 * because Room's KSP processor can only resolve the cross-module [UserEntity] foreign key from a
 * module that depends on both `:data:textadventure` and `:data:account`, exactly as `:app` does.
 *
 * [getUserDao] is exposed so tests can seed the [UserEntity] that the adventures' foreign key
 * requires before inserting adventure rows.
 */
@Database(
    entities = [
        UserEntity::class,
        AdventureEntity::class,
        MessageEntity::class,
        SentenceEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class TextAdventureTestDatabase : RoomDatabase() {
    abstract fun getAdventureDao(): AdventureLocalDataSource
    abstract fun getUserDao(): UserLocalDataSource
}
