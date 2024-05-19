package `in`.comprehensible.data.sources

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import `in`.comprehensible.data.stories.model.Story
import `in`.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import `in`.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSourceModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeStoriesLocalDataSource @Inject constructor() : StoriesLocalDataSource {
    var story = Story("title", "content")
    override suspend fun getStory(): Story {
        return story
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
