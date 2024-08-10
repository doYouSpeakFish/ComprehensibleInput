package input.comprehensible.data.stories.sources.stories.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import input.comprehensible.data.stories.sources.stories.model.StoryData
import javax.inject.Singleton

/**
 * A data source for stories that are retrieved from a remote server.
 */
interface StoriesRemoteDataSource {
    /**
     * Generates a story using AI.
     */
    suspend fun generateAiStory(
        learningLanguage: String,
        translationLanguage: String,
    ): AiStoryData?
}

@Module
@InstallIn(SingletonComponent::class)
class StoriesRemoteDataSourceModule {
    @Provides
    @Singleton
    fun provideStoriesRemoteDataSource(): StoriesRemoteDataSource = DefaultStoriesRemoteDataSource()
}

data class AiStoryData(
    val content: StoryData,
    val translations: StoryData,
)
