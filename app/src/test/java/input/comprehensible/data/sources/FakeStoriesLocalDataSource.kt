package input.comprehensible.data.sources

import android.graphics.Bitmap
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSourceModule
import input.comprehensible.data.stories.sources.stories.local.StoryData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeStoriesLocalDataSource @Inject constructor() : StoriesLocalDataSource {
    var stories = mapOf<String, List<StoryData>>()

    override suspend fun getStory(id: String, language: String): StoryData? =
        stories[language]?.first { it.id == id }

    override suspend fun getStories(learningLanguage: String): List<StoryData> =
        stories[learningLanguage] ?: emptyList()

    override suspend fun loadStoryImage(storyId: String, path: String): Bitmap =
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
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
