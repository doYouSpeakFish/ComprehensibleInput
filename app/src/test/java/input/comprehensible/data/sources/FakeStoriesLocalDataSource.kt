package input.comprehensible.data.sources

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSourceModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeStoriesLocalDataSource @Inject constructor() : StoriesLocalDataSource {
    var stories = listOf<Story>()
    override suspend fun getStory(id: String): Story {
        return stories.first { it.id == id }
    }

    override suspend fun getStories(): StoriesList {
        return StoriesList(
            stories = stories.map {
                StoriesList.StoriesItem(
                    id = it.id,
                    title = it.title,
                )
            }
        )
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
