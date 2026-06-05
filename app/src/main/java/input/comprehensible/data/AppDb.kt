package input.comprehensible.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ktin.Singleton
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import input.comprehensible.di.ApplicationProvider

@Database(
    entities = [
        StoryEntity::class,
    ],
    version = 8,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ]
)
abstract class AppDb : RoomDatabase() {
    abstract fun getStoriesInfoDao(): StoriesInfoLocalDataSource

    companion object : Singleton<AppDb>() {
        override fun create(): AppDb {
            val builder = Room.databaseBuilder<AppDb>(context = ApplicationProvider(), name = "app-db")
            dropTextAdventureMigrations().forEach { builder.addMigrations(it) }
            return builder.build()
        }
    }
}

/**
 * The text adventure prototype added tables and views across schema versions 4-7 before it was
 * removed. Any install still on one of those versions must drop those objects to reach version 8,
 * so one migration is registered per source version. Each one discovers and drops whatever
 * `TextAdventure*` tables and views that version happened to leave behind, which stays correct
 * across the prototype's several intermediate shapes without having to enumerate them.
 */
internal fun dropTextAdventureMigrations(): Array<Migration> =
    (TEXT_ADVENTURE_FIRST_VERSION until POST_TEXT_ADVENTURE_VERSION)
        .map(::DropTextAdventureMigration)
        .toTypedArray()

internal class DropTextAdventureMigration(from: Int) : Migration(from, POST_TEXT_ADVENTURE_VERSION) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.dropTextAdventureObjects(type = "view")
        db.dropTextAdventureObjects(type = "table")
    }
}

private fun SupportSQLiteDatabase.dropTextAdventureObjects(type: String) {
    val names = query(
        "SELECT name FROM sqlite_master WHERE type = ? AND name LIKE 'TextAdventure%'",
        arrayOf<Any?>(type),
    ).use { cursor ->
        buildList { while (cursor.moveToNext()) add(cursor.getString(0)) }
    }
    names.forEach { execSQL("DROP ${type.uppercase()} IF EXISTS `$it`") }
}

private const val TEXT_ADVENTURE_FIRST_VERSION = 4
private const val POST_TEXT_ADVENTURE_VERSION = 8
