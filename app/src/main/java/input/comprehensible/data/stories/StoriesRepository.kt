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
            stories = storiesWithTranslations.mapNotNull { (story, translation) ->
                StoriesList.StoriesItem(
                    id = story.id,
                    title = story.title,
                    titleTranslated = translation.title,
                    featuredImage = storiesLocalDataSource.loadStoryImage(
                        storyId = story.id,
                        path = story.featuredImagePath,
                    ) ?: return@mapNotNull null,
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
    ): StoryResult {
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
            return StoryResult.Failure.StoryMissing(language = learningLanguage)
        }
        val translatedStoryData = storiesLocalDataSource.getStory(
            id = id,
            language = translationsLanguage
        ) ?: run {
            Timber.e("Translation $translationsLanguage not found for story $id")
            return StoryResult.Failure.TranslationMissing(language = translationsLanguage)
        }
        return storyData.toStory(
            id = id,
            translation = translatedStoryData,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
            position = storyInfo.position,
        )
            ?.let { StoryResult.Success(it) }
            ?: StoryResult.Failure.ContentMismatch(
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
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
        val learningPart = parts.firstOrNull { it.id == startPartId }
            ?: run {
                Timber.e("Story $id is missing part with id $startPartId")
                return null
            }

        val translationPart = translation.parts.firstOrNull { it.id == startPartId }
            ?: run {
                Timber.e("Story $id translation is missing part with id $startPartId")
                return null
            }

        if (learningPart.content.size != translationPart.content.size) {
            Timber.e(
                "Story $id content could not be fully matched between $learningLanguage and $translationsLanguage"
            )
            return null
        }

        val storyElements = learningPart.content
            .zip(translationPart.content)
            .map { (storyElementData, translationElement) ->
                storyElementData.toStoryElement(
                    storyId = id,
                    translation = translationElement,
                    learningLanguage = learningLanguage,
                    translationsLanguage = translationsLanguage,
                ) ?: return null
            }

        return Story(
            id = id,
            title = title,
            translatedTitle = translation.title,
            content = storyElements,
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
                val translationParagraph = translation as? StoryElementData.ParagraphData ?: run {
                    Timber.e("No matching translation found for paragraph in story $storyId")
                    return null
                }
                this.toStoryElement(
                    storyId = storyId,
                    translationParagraph = translationParagraph,
                    learningLanguage = learningLanguage,
                    translationsLanguage = translationsLanguage
                )
            }

            is StoryElementData.ImageData -> {
                this.toStoryElement(storyId = storyId)
            }
        }
    }

    private fun StoryElementData.ParagraphData.toStoryElement(
        storyId: String,
        translationParagraph: StoryElementData.ParagraphData,
        learningLanguage: String,
        translationsLanguage: String,
    ): StoryElement.Paragraph? {
        if (sentences.size != translationParagraph.sentences.size) {
            Timber.e(
                "Mismatched number of sentences in story $storyId for languages " +
                        "$learningLanguage and $translationsLanguage"
            )
            return null
        }
        return StoryElement.Paragraph(
            sentences = sentences,
            sentencesTranslations = translationParagraph.sentences
        )
    }

    private suspend fun StoryElementData.ImageData.toStoryElement(
        storyId: String,
    ): StoryElement.Image? {
        return StoryElement.Image(
            contentDescription = contentDescription,
            bitmap = storiesLocalDataSource.loadStoryImage(
                storyId = storyId,
                path = path,
            ) ?: return null,
        )
    }

}
sealed interface StoryResult {
    data class Success(val story: Story) : StoryResult

    sealed interface Failure : StoryResult {
        data class StoryMissing(val language: String) : Failure
        data class TranslationMissing(val language: String) : Failure
        data class ContentMismatch(
            val learningLanguage: String,
            val translationsLanguage: String,
        ) : Failure
    }
}
