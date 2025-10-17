package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for stories.
 */
@Singleton
class StoriesRepository @Inject constructor(
    private val storiesLocalDataSource: StoriesLocalDataSource,
    private val storiesInfoLocalDataSource: StoriesInfoLocalDataSource,
) {

    suspend fun storiesList(
        learningLanguage: String,
        translationsLanguage: String,
    ): StoriesList {
        val stories = storiesLocalDataSource
            .getStories(learningLanguage = learningLanguage)
            .sortedByDescending { it.id }
        val translations = storiesLocalDataSource
            .getStories(learningLanguage = translationsLanguage)
            .associateBy { it.id }
        val storiesWithTranslations = buildList {
            stories.forEach { story ->
                val translation = translations[story.id]
                if (translation != null) {
                    add(story to translation)
                }
            }
        }
        return StoriesList(
            stories = storiesWithTranslations.map { (story, translation) ->
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
    }

    /**
     * Gets a story in the given [learningLanguage] with translations in the given
     * [translationsLanguage].
     */
    suspend fun getStory(
        id: String,
        learningLanguage: String,
        translationsLanguage: String
    ): Story? {
        val storyInfo = storiesInfoLocalDataSource.getStory(id)
            ?: StoryEntity(id = id, position = 0).also {
                // First time story opened. Insert info into db so this story can be tracked
                storiesInfoLocalDataSource.insertStory(story = it)
            }
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
        return storyData.toStory(
            id = id,
            translation = translatedStoryData,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
            position = storyInfo.position,
        )
    }

    suspend fun updateStoryPosition(id: String, position: Int) {
        storiesInfoLocalDataSource.updateStory(
            story = StoryEntity(id = id, position = position)
        )
    }

    private suspend fun StoryData.toStory(
        id: String,
        translation: StoryData,
        learningLanguage: String,
        translationsLanguage: String,
        position: Int,
    ): Story? {
        return Story(
            id = id,
            title = title,
            translatedTitle = translation.title,
            content = content
                .zip(translation.content)
                .map { (storyElementData, translation) ->
                    storyElementData.toStoryElement(
                        storyId = id,
                        translation = translation,
                        learningLanguage = learningLanguage,
                        translationsLanguage = translationsLanguage,
                    ) ?: return null
                },
            currentStoryElementIndex = position,
        )
    }

    private suspend fun StoryElementData.toStoryElement(
        storyId: String,
        translation: StoryElementData,
        learningLanguage: String,
        translationsLanguage: String,
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
