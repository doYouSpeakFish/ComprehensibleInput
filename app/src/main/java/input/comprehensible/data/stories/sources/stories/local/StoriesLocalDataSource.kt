package input.comprehensible.data.stories.sources.stories.local

import android.graphics.Bitmap
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    fun getStories(learningLanguage: String): Flow<List<StoryData>>

    /**
     * Loads an image from the assets folder.
     */
    suspend fun loadStoryImage(storyId: String, path: String): Bitmap?
}


/**
 * A story.
 */
@Serializable
data class StoryData(
    val id: String,
    val title: String,
    val featuredImagePath: String,
    val start: StoryPartData,
)

@Serializable
data class StoryPartData(
    val id: String,
    val content: List<StoryElementData>,
    val choices: List<StoryChoiceData> = emptyList(),
)

@Serializable
data class StoryChoiceData(
    val text: String,
    val part: StoryPartData,
)

/**
 * An element of a story.
 */
@Serializable
sealed interface StoryElementData {
    /**
     * A paragraph of text.
     */
    @Serializable
    @SerialName("paragraph")
    data class ParagraphData(val sentences: List<String>) : StoryElementData

    /**
     * An image, with [contentDescription] and [path] to the image file.
     */
    @Serializable
    @SerialName("image")
    data class ImageData(
        val contentDescription: String,
        val path: String,
    ) : StoryElementData
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
