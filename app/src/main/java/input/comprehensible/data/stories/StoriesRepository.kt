package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for stories.
 */
@Singleton
class StoriesRepository @Inject constructor(
    private val storiesLocalDataSource: StoriesLocalDataSource
) {
    val storiesList = flow {
        emit(StoriesList(stories = emptyList()))
        emit(storiesLocalDataSource.getStories())
    }

    /**
     * Gets a story.
     */
    suspend fun getStory(id: String) = storiesLocalDataSource.getStory(id)
}