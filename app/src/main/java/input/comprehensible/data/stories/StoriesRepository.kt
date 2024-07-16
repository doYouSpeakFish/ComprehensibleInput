package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.di.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
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
    private val translationsLanguage = "en"

    val storiesList: StateFlow<StoriesList> = flow {
        val stories = storiesLocalDataSource.getStories(learningLanguage = learningLanguage)
        val translations =
            storiesLocalDataSource.getStories(learningLanguage = translationsLanguage)
        val storiesList = StoriesList(
            stories = stories.zip(translations).map { (story, translation) ->
                val featuredImage = story
                    .content
                    .filterIsInstance<StoryElementData.ImageData>()
                    .first()
                StoriesList.StoriesItem(
                    id = story.id,
                    title = story.title,
                    titleTranslated = translation.title,
                    featuredImage = storiesLocalDataSource.loadStoryImage(
                        storyId = story.id,
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
        ) ?: run {
            Timber.e("Story $id not found for language $learningLanguage")
            return null
        }
        val translatedStoryData = storiesLocalDataSource.getStory(
            id = id,
            language = translationsLanguage
        ) ?: run {
            Timber.e("Translation $translationsLanguage not found for story $id")
            return null
        }
        return storyData.toStory(id = id, translation = translatedStoryData)
    }

    private suspend fun StoryData.toStory(id: String, translation: StoryData): Story? {
        return Story(
            id = id,
            title = title,
            translatedTitle = translation.title,
            content = content
                .zip(translation.content)
                .map { (storyElementData, translation) ->
                    storyElementData.toStoryElement(
                        storyId = id,
                        translation = translation
                    ) ?: return null
                }
        )
    }

    private suspend fun StoryElementData.toStoryElement(
        storyId: String,
        translation: StoryElementData
    ): StoryElement? {
        return when (this) {
            is StoryElementData.ParagraphData -> {
                (translation as? StoryElementData.ParagraphData)
                    ?: run {
                        Timber.e("No matching translation found for paragraph in story $storyId")
                        return null
                    }
                if (sentences.size != translation.sentences.size) {
                    Timber.e(
                        "Mismatched number of sentences in story $storyId for languages " +
                                "$learningLanguage and $translationsLanguage"
                    )
                    return null
                }
                StoryElement.Paragraph(
                    sentences = sentences,
                    sentencesTranslations = translation.sentences
                )
            }

            is StoryElementData.ImageData -> StoryElement.Image(
                contentDescription = contentDescription,
                bitmap = storiesLocalDataSource.loadStoryImage(
                    storyId = storyId,
                    path = path,
                )
            )
        }
    }
}