package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.model.StoryData
import input.comprehensible.data.stories.sources.stories.model.StoryElementData
import input.comprehensible.data.stories.sources.stories.remote.StoriesRemoteDataSource
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for stories.
 */
@Singleton
class StoriesRepository @Inject constructor(
    private val storiesLocalDataSource: StoriesLocalDataSource,
    private val storiesRemoteDataSource: StoriesRemoteDataSource,
) {

    suspend fun storiesList(
        learningLanguage: String,
        translationsLanguage: String,
    ): StoriesList {
        val stories = storiesLocalDataSource
            .getStories(learningLanguage = learningLanguage)
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

    suspend fun getAiStory(
        learningLanguage: String,
        translationsLanguage: String
    ): Story? {
        Timber.d("Generating AI story for $learningLanguage and $translationsLanguage")
        val aiStoryData = storiesRemoteDataSource.generateAiStory(
            learningLanguage = learningLanguage,
            translationLanguage = translationsLanguage,
        )
        return aiStoryData.content.toStory(
            id = aiStoryData.content.id,
            translation = aiStoryData.translations,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
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
            translationsLanguage = translationsLanguage
        )
    }

    private suspend fun StoryData.toStory(
        id: String,
        translation: StoryData,
        learningLanguage: String,
        translationsLanguage: String
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
                }
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