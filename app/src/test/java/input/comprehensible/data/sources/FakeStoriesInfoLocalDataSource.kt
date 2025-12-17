package input.comprehensible.data.sources

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoModule
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeStoriesInfoLocalDataSource @Inject constructor() : StoriesInfoLocalDataSource {
    private val stories = MutableStateFlow<Map<String, StoryEntity>>(emptyMap())

    override suspend fun insertStory(story: StoryEntity) {
        stories.update { current -> current + (story.id to story) }
    }

    override fun getStory(id: String): Flow<StoryEntity?> = stories.map { it[id] }

    override suspend fun updateStory(id: String, position: Int) {
        stories.update { current ->
            val oldStoryInfo = current.getValue(id)
            val newStoryInfo = oldStoryInfo.copy(position = position)
            current + (id to newStoryInfo)
        }
    }

    override suspend fun updateStory(id: String, partId: String) {
        stories.update { current ->
            val oldStoryInfo = current.getValue(id)
            val newStoryInfo = oldStoryInfo.copy(partId = partId, position = 0)
            current + (id to newStoryInfo)
        }
    }

    override suspend fun updateStoryChoice(
        id: String,
        lastChosenPartId: String,
    ) {
        stories.update { current ->
            val oldStoryInfo = current.getValue(id)
            val newStoryInfo = oldStoryInfo.copy(
                lastChosenPartId = lastChosenPartId,
            )
            current + (id to newStoryInfo)
        }
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
