package input.comprehensible.data.stories.sources.stories.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import input.comprehensible.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber
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
    ): StoryData? = withContext(dispatcher) {
        runCatching {
            context.assets
                .open("stories/$id/$language.json")
                .use { Json.decodeFromStream<StoryData>(it) }
        }
            .onFailure { Timber.d(it, "Failed to load story $id") }
            .getOrNull()
    }

    override suspend fun getStories(
        learningLanguage: String
    ): List<StoryData> = withContext(dispatcher) {
        context.assets
            .list("stories")
            .orEmpty()
            .mapNotNull { storyId ->
                getStory(id = storyId, language = learningLanguage)
            }
    }

    override suspend fun loadStoryImage(
        storyId: String,
        path: String
    ): Bitmap = withContext(dispatcher) {
        context
            .assets
            .open("stories/$storyId/$path")
            .use { BitmapFactory.decodeStream(it) }
    }

    override suspend fun importStory(): StoryData? {
        TODO("Not yet implemented")
    }
}
