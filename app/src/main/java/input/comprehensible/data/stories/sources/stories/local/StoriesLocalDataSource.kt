package input.comprehensible.data.stories.sources.stories.local

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.stories.model.Story
import javax.inject.Singleton

/**
 * A data source for stories that are stored locally.
 */
interface StoriesLocalDataSource {
    /**
     * Gets a story from the local data source.
     */
    suspend fun getStory(id: String): Story
}

/**
 * Hilt module for injecting the default implementation of [StoriesLocalDataSource].
 */
@Module
@InstallIn(SingletonComponent::class)
class StoriesLocalDataSourceModule {
    @Provides
    @Singleton
    fun provideStoriesLocalDataSource(): StoriesLocalDataSource = DefaultStoriesLocalDataSource()
}
