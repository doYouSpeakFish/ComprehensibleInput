package input.comprehensible.data.stories.sources.stories.local

import android.graphics.Bitmap
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.stories.sources.stories.model.StoryData
import javax.inject.Singleton

/**
 * A data source for stories that are stored locally.
 */
interface StoriesLocalDataSource {
    /**
     * Gets a story from the local data source.
     */
    suspend fun getStory(id: String, language: String): StoryData?

    /**
     * Gets a list of stories from the local data source.
     */
    suspend fun getStories(learningLanguage: String): List<StoryData>

    /**
     * Loads an image from the assets folder.
     */
    suspend fun loadStoryImage(storyId: String, path: String): Bitmap
}


/**
 * Hilt module for injecting the default implementation of [StoriesLocalDataSource].
 */
@Module
@InstallIn(SingletonComponent::class)
interface StoriesLocalDataSourceModule {
    @Binds
    @Singleton
    fun provideStoriesLocalDataSource(
        defaultStoriesLocalDataSource: DefaultStoriesLocalDataSource
    ): StoriesLocalDataSource
}
