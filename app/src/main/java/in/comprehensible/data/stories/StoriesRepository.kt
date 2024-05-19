package `in`.comprehensible.data.stories

import `in`.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for stories.
 */
@Singleton
class StoriesRepository @Inject constructor(
    private val storiesLocalDataSource: StoriesLocalDataSource
) {
    /**
     * Gets a story.
     */
    suspend fun getStory() = storiesLocalDataSource.getStory()
}