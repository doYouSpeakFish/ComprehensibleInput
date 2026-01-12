package input.comprehensible.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity

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

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room
                .databaseBuilder<AppDb>(context = context, name = "app-db")
                .build()
                .also { INSTANCE = it }
        }
    }
}
