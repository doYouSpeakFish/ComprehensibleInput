package input.comprehensible.data.stories.sources.stories.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import input.comprehensible.di.ApplicationProvider
import input.comprehensible.di.IoDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber

/**
 * Default implementation of [StoriesLocalDataSource] that provides the story content.
 */
@OptIn(ExperimentalSerializationApi::class)
class DefaultStoriesLocalDataSource(
    private val dispatcher: CoroutineDispatcher = IoDispatcherProvider(),
    private val context: Context = ApplicationProvider(),
) : StoriesLocalDataSource {
    override suspend fun getStory(
        id: String,
        language: String
    ): StoryData? = withContext(dispatcher) {
        runCatching {
            context.assets
                .open("stories/$id/$language.json")
                .use { Json.decodeFromStream<StoryData>(it) }
        }
            .onFailure { Timber.d(it, "Failed to load story $id") }
            .getOrNull()
    }

    override fun getStories(
        learningLanguage: String
    ): Flow<List<StoryData>> = flow {
        val stories = context.assets
            .list("stories")
            .orEmpty()
            .mapNotNull { storyId ->
                getStory(id = storyId, language = learningLanguage)
            }
        emit(stories)
    }.flowOn(dispatcher)

    override suspend fun loadStoryImage(
        storyId: String,
        path: String
    ): Bitmap? = withContext(dispatcher) {
        runCatching {
            context
                .assets
                .open("stories/$storyId/$path")
                .use { BitmapFactory.decodeStream(it) }
        }
            .onFailure {
                Timber.e(it, "Failed to load image $path for story $storyId")
            }
            .getOrNull()
    }
}
