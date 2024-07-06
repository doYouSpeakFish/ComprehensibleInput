package input.comprehensible.data.stories.sources.stories.local

import androidx.room.Dao
import androidx.room.Query
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.db.StoriesDB
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

/**
 * Data access object for stories in the local database.
 */
@Dao
interface StoriesLocalDataSource {
    /**
     * Gets the list of all stories in the local database, with the titles in the specified
     * languages.
     */
    @Query(
        """
            SELECT * FROM StoryEntity 
            JOIN StoryTitleEntity ON StoryEntity.id = StoryTitleEntity.storyId 
            WHERE language = :learningLanguage
            OR language = :nativeLanguage
            ORDER BY id ASC
        """
    )
    fun getStories(
        learningLanguage: String,
        nativeLanguage: String
    ): Flow<Map<StoryEntity, List<StoryTitleEntity>>>

    /**
     * Gets all elements of the story with the specified ID and language.
     */
    @Query(
        """
            SELECT * FROM StoryEntity
            JOIN StoryElementEntity ON StoryEntity.id = StoryElementEntity.storyId
            WHERE storyId = :storyId 
            AND language = :language 
            ORDER BY position ASC
        """
    )
    suspend fun getStoryElements(
        storyId: String,
        language: String,
    ): Map<StoryTitleEntity, List<StoryElementEntity>>
}

@Module
@InstallIn(SingletonComponent::class)
class StoriesLocalDataSourceModule {
    @Provides
    @Singleton
    fun provideStoriesLocalDataSource(
        storiesDB: StoriesDB
    ): StoriesLocalDataSource = storiesDB.storiesDao()
}
