package input.comprehensible.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryElementEntity
import input.comprehensible.data.stories.sources.stories.local.StoryEntity
import input.comprehensible.data.stories.sources.stories.local.StoryTitleEntity
import input.comprehensible.di.AppScope
import input.comprehensible.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Database(
    entities = [
        StoryEntity::class,
        StoryTitleEntity::class,
        StoryElementEntity::class
    ],
    version = 1
)
abstract class StoriesDB : RoomDatabase() {
    abstract fun storiesDao(): StoriesLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
class StoriesDBModule {
    @Singleton
    @Provides
    fun provideStoriesDB(
        @ApplicationContext context: Context,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        @AppScope scope: CoroutineScope,
    ): StoriesDB {
        return Room.databaseBuilder(
            context = context,
            StoriesDB::class.java,
            "stories.db"
        ).build()
    }
}
