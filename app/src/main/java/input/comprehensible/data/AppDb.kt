package input.comprehensible.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ktin.Singleton
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureSentenceEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import input.comprehensible.di.ApplicationProvider

@Database(
    entities = [
        StoryEntity::class,
        TextAdventureEntity::class,
        TextAdventureMessageEntity::class,
        TextAdventureSentenceEntity::class,
    ],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5, spec = TextAdventureMigration4To5::class),
    ]
)
abstract class AppDb : RoomDatabase() {
    abstract fun getStoriesInfoDao(): StoriesInfoLocalDataSource
    abstract fun getTextAdventuresDao(): TextAdventuresLocalDataSource

    companion object : Singleton<AppDb>() {
        override fun create() = Room
            .databaseBuilder<AppDb>(context = ApplicationProvider(), name = "app-db")
            .addMigrations(TextAdventureMigration5To6())
            .build()
    }
}
