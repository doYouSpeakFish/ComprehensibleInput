package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.di.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for stories.
 */
@Singleton
class StoriesRepository @Inject constructor(
    private val storiesLocalDataSource: StoriesLocalDataSource,
    @AppScope val scope: CoroutineScope
) {
    private val learningLanguage = "de"

    val storiesList = flow {
        emit(storiesLocalDataSource.getStories(learningLanguage = learningLanguage))
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = StoriesList(stories = emptyList())
        )

    /**
     * Gets a story.
     */
    suspend fun getStory(id: String) = storiesLocalDataSource.getStory(
        id = id,
        language = learningLanguage
    )
}