package input.comprehensible.data.sources

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoModule
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeStoriesInfoLocalDataSource @Inject constructor() : StoriesInfoLocalDataSource {
    private val stories = mutableMapOf<String, StoryEntity>()

    override suspend fun insertStory(story: StoryEntity) {
        stories[story.id] = story
    }

    override suspend fun getStory(id: String): StoryEntity? = stories[id]

    override suspend fun updateStory(story: StoryEntity) {
        stories[story.id] = story
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [StoriesInfoModule::class]
)
interface FakeStoriesInfoLocalDataSourceModule {
    @Binds
    @Singleton
    fun provideStoriesInfoLocalDataSource(
        fakeStoriesLocalDataSource: FakeStoriesInfoLocalDataSource
    ): StoriesInfoLocalDataSource
}
