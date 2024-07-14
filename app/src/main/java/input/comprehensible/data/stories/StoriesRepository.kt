package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
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
        val stories = storiesLocalDataSource.getStories(learningLanguage = learningLanguage)
        val storiesList = StoriesList(
            stories = stories.map {
                val featuredImage = it
                    .content
                    .filterIsInstance<StoryElementData.ImageData>()
                    .first()
                StoriesList.StoriesItem(
                    id = it.id,
                    title = it.title,
                    featuredImage = storiesLocalDataSource.loadStoryImage(
                        storyId = it.id,
                        path = featuredImage.path,
                    ),
                    featuredImageContentDescription = featuredImage.contentDescription,
                )
            }
        )
        emit(storiesList)
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = StoriesList(stories = emptyList())
        )

    /**
     * Gets a story.
     */
    suspend fun getStory(id: String): Story? {
        val storyData = storiesLocalDataSource.getStory(
            id = id,
            language = learningLanguage
        ) ?: return null
        val story = Story(
            id = storyData.id,
            title = storyData.title,
            content = storyData.content.map {
                when (it) {
                    is StoryElementData.ParagraphData -> StoryElement.Paragraph(
                        text = it.text
                    )

                    is StoryElementData.ImageData -> StoryElement.Image(
                        contentDescription = it.contentDescription,
                        bitmap = storiesLocalDataSource.loadStoryImage(
                            storyId = storyData.id,
                            path = it.path,
                        )
                    )
                }
            }
        )
        return story
    }
}