package input.comprehensible.data.stories.sources.storyinfo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Update
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.AppDb
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import javax.inject.Singleton

@Dao
interface StoriesInfoLocalDataSource {
    @Insert(onConflict = IGNORE)
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

fun StoriesInfoLocalDataSource.getOrCreateStory(
    id: String
): Flow<StoryEntity> = getStory(id)
    .onEach { storyEntity ->
        if (storyEntity == null) {
            insertStory(StoryEntity(id = id))
        }
    }
    .filterNotNull()
    .distinctUntilChanged()

@Module
@InstallIn(SingletonComponent::class)
class StoriesInfoModule {
    @Provides
    @Singleton
    fun provideStoriesInfoLocalDataSource(appDb: AppDb): StoriesInfoLocalDataSource =
        appDb.getStoriesInfoDao()
}
