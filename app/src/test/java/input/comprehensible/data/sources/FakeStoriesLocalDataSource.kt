package input.comprehensible.data.sources

import android.graphics.Bitmap
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSourceModule
import input.comprehensible.data.stories.sources.stories.local.StoryData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeStoriesLocalDataSource @Inject constructor() : StoriesLocalDataSource {
    var stories = mapOf<String, List<StoryData>>()
    var storyLoadDelayMillis: Long = 0L
    var storyIdsWithoutImages: Set<String> = emptySet()

    override suspend fun getStory(id: String, language: String): StoryData? {
        if (storyLoadDelayMillis > 0L) {
            delay(storyLoadDelayMillis)
        }
        return stories[language]?.firstOrNull { it.id == id }
    }

    override fun getStories(learningLanguage: String): Flow<List<StoryData>> = flow {
        emit(stories[learningLanguage] ?: emptyList())
    }

    override suspend fun loadStoryImage(storyId: String, path: String): Bitmap? {
        if (storyId in storyIdsWithoutImages) return null
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [StoriesLocalDataSourceModule::class]
)
interface FakeStoriesLocalDataSourceModule {
    @Binds
    @Singleton
    fun provideStoriesLocalDataSource(
        fakeStoriesLocalDataSource: FakeStoriesLocalDataSource
    ): StoriesLocalDataSource
}
