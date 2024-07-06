package input.comprehensible.data.stories.sources.stories.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import input.comprehensible.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

/**
 * Default implementation of [StoriesLocalDataSource] that provides the story content.
 */
@OptIn(ExperimentalSerializationApi::class)
class PreLoadedStoriesLocalDataSource @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
) {

    suspend fun getStory(id: String) = withContext(dispatcher) {
        context.assets
            .open("$id/story.json")
            .use { Json.decodeFromStream<StoryData>(it) }
    }

    suspend fun getStories() = withContext(dispatcher) {
        context.assets
            .open("story_list.json")
            .use { Json.decodeFromStream<StoriesListData>(it) }
    }
}

/**
 * A list of stories retrieved from local storage.
 */
@Serializable
data class StoriesListData(
    val stories: List<StoryItemData>,
)

/**
 * A story list item retrieved from local storage.
 */
@Serializable
data class StoryItemData(
    val id: String,
    val title: String,
    val subtitle: String,
    val featuredImage: FeaturedImage,
)

/**
 * The featured image of a story list item retrieved from local storage.
 */
@Serializable
data class FeaturedImage(
    val path: String,
    val contentDescription: String,
)

/**
 * A story.
 */
@Serializable
data class StoryData(
    val id: String,
    val title: String,
    val content: List<StoryElementData>,
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
    data class ParagraphData(val text: String) : StoryElementData

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
