package input.comprehensible.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.di.singleton.Singleton
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import input.comprehensible.di.ApplicationProvider

@Database(
    entities = [StoryEntity::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
    ]
)
abstract class AppDb : RoomDatabase() {
    abstract fun getStoriesInfoDao(): StoriesInfoLocalDataSource

    companion object : Singleton<AppDb>() {
        override fun create() = Room
            .databaseBuilder<AppDb>(context = ApplicationProvider(), name = "app-db")
            .build()
    }
}
