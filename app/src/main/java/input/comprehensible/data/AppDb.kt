package input.comprehensible.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ktin.Singleton
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import input.comprehensible.data.textadventure.sources.local.AdventureEntity
import input.comprehensible.data.textadventure.sources.local.AdventureLocalDataSource
import input.comprehensible.data.textadventure.sources.local.MessageEntity
import input.comprehensible.data.textadventure.sources.local.SentenceEntity
import input.comprehensible.data.user.UserEntity
import input.comprehensible.di.ApplicationProvider

@Database(
    entities = [
        StoryEntity::class,
        UserEntity::class,
        AdventureEntity::class,
        MessageEntity::class,
        SentenceEntity::class,
    ],
    version = 8,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5, spec = TextAdventureMigration4To5::class),
        AutoMigration(from = 7, to = 8, spec = RemoveTextAdventurePrototypeSpec::class),
    ]
)
abstract class AppDb : RoomDatabase() {
    abstract fun getStoriesInfoDao(): StoriesInfoLocalDataSource
    abstract fun getUserDao(): UserLocalDataSource
    abstract fun getAdventureDao(): AdventureLocalDataSource

    companion object : Singleton<AppDb>() {
        override fun create() = Room
            .databaseBuilder<AppDb>(context = ApplicationProvider(), name = "app-db")
            .addMigrations(TextAdventureMigration5To6())
            .addMigrations(TextAdventureMigration6To7())
            .build()
    }
}
