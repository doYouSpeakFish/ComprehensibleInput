package input.comprehensible.data.stories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryElementEntity
import input.comprehensible.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for stories.
 */
@Singleton
class StoriesRepository @Inject constructor(
    private val storiesLocalDataSource: StoriesLocalDataSource,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    val storiesList = storiesLocalDataSource.getStories(
        learningLanguage = "de",
        nativeLanguage = "en"
    ).map { storiesToTitles ->
        StoriesList(
            stories = storiesToTitles.mapNotNull { (story, titles) ->
                val title = titles.first { it.language == "de" }
                StoriesList.StoriesItem(
                    id = story.id,
                    title = title.title,
                    subtitle = title.subtitle,
                    featuredImage = title.featureImagePath?.let { loadImage(it) }
                        ?: return@map null,
                    featuredImageContentDescription = title.featureImageContentDescription
                        ?: return@map null
                )
            }
        )
    }

    /**
     * Gets a story.
     */
    suspend fun getStory(id: String) = storiesLocalDataSource.getStoryElements(
        storyId = id,
        language = "de"
    )
        .entries
        .firstOrNull()
        ?.let { (story, elements) ->
            Story(
                id = id,
                title = story.title,
                content = elements.flatMap { it.toStoryElements() }
            )
        }

    private suspend fun StoryElementEntity.toStoryElements(): List<StoryElement> {
        return buildList {
            if (imageContentDescription != null) {
                val bitmap = imagePath?.let { loadImage(it) }
                if (bitmap != null) {
                    add(StoryElement.Image(imageContentDescription, bitmap))
                }
            }
            add(StoryElement.Paragraph(text))
        }
    }

    private suspend fun loadImage(imagePath: String): Bitmap? = withContext(dispatcher) {
        context
            .assets
            .open(imagePath)
            .use { BitmapFactory.decodeStream(it) }
    }
}