package input.comprehensible.data.stories.sources.stories.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
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
class DefaultStoriesLocalDataSource @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
) : StoriesLocalDataSource {
    override suspend fun getStory(
        id: String,
        language: String
    ): Story = withContext(dispatcher) {
        context.assets
            .open("stories/$id/$language.json")
            .use { Json.decodeFromStream<StoryData>(it) }
            .toStory()
    }

    override suspend fun getStories(
        learningLanguage: String
    ): StoriesList = withContext(dispatcher) {
        StoriesList(
            stories = context.assets
                .list("stories")
                .orEmpty()
                .map { storyId ->
                    context.assets
                        .open("stories/$storyId/$learningLanguage.json")
                        .use { Json.decodeFromStream<StoryData>(it) }
                        .toStoryListItem()
                }
        )
    }

    private fun StoryData.toStoryListItem(): StoriesList.StoriesItem {
        val featureImageData = content
            .filterIsInstance<StoryElementData.ImageData>()
            .first()
        return StoriesList.StoriesItem(
            id = id,
            title = title,
            subtitle = title,
            featuredImage = loadImageFromAssets(path = "stories/$id/${featureImageData.path}"),
            featuredImageContentDescription = featureImageData.contentDescription,
        )
    }

    private fun StoryData.toStory() = Story(
        id = id,
        title = title,
        content = content.map { it.toStoryElement(id) }
    )

    private fun StoryElementData.toStoryElement(storyId: String) = when (this) {
        is StoryElementData.ParagraphData -> StoryElement.Paragraph(text)
        is StoryElementData.ImageData -> StoryElement.Image(
            contentDescription = contentDescription,
            bitmap = loadImageFromAssets("stories/$storyId/$path")
        )
    }

    private fun loadImageFromAssets(path: String): Bitmap = context
        .assets
        .open(path)
        .use { BitmapFactory.decodeStream(it) }
}

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
