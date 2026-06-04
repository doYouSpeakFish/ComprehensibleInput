package input.comprehensible.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ktin.Singleton
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import input.comprehensible.data.UserEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureMessageSentenceView
import input.comprehensible.data.textadventures.sources.local.TextAdventureSentenceEntity
import input.comprehensible.data.textadventures.sources.local.TextAdventureSummaryView
import input.comprehensible.data.account.UserDao
import input.comprehensible.data.textadventures.sources.local.TextAdventuresLocalDataSource
import input.comprehensible.di.ApplicationProvider

@Database(
    entities = [
        StoryEntity::class,
        UserEntity::class,
        TextAdventureEntity::class,
        TextAdventureMessageEntity::class,
        TextAdventureSentenceEntity::class,
    ],
    views = [
        TextAdventureSummaryView::class,
        TextAdventureMessageSentenceView::class,
    ],
    version = 9,
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
    abstract fun getUserDao(): UserDao

    companion object : Singleton<AppDb>() {
        override fun create() = Room
            .databaseBuilder<AppDb>(context = ApplicationProvider(), name = "app-db")
            .addMigrations(TextAdventureMigration5To6())
            .addMigrations(TextAdventureMigration6To7())
            .addMigrations(TextAdventureMigration7To8())
            .addMigrations(TextAdventureMigration8To9())
            .build()
    }
}
