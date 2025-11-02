package input.comprehensible.data.stories.sources.storyinfo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.AppDb
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
interface StoriesInfoLocalDataSource {
    @Insert
    suspend fun insertStory(story: StoryEntity)

    @Query(
        """
            SELECT * FROM StoryEntity
            WHERE id = :id
            LIMIT 1
        """
    )
    fun getStory(id: String): Flow<StoryEntity?>

    @Update
    suspend fun updateStory(story: StoryEntity)
}

@Module
@InstallIn(SingletonComponent::class)
class StoriesInfoModule {
    @Provides
    @Singleton
    fun provideStoriesInfoLocalDataSource(appDb: AppDb): StoriesInfoLocalDataSource =
        appDb.getStoriesInfoDao()
}
