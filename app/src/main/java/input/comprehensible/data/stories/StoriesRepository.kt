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
                val hasStartPart = story.parts.any { it.id == story.startPartId }
                if (!hasStartPart) {
                    Timber.e(
                        "Story ${story.id} is missing a part for startPartId ${story.startPartId}"
                    )
                    return@mapNotNull null
                }
                val featuredImage = storiesLocalDataSource.loadStoryImage(
                    storyId = story.id,
                    path = story.featuredImagePath,
                ) ?: run {
                    Timber.e(
                        "Story ${story.id} is missing an image with path ${story.featuredImagePath}"
                    )
                    return@mapNotNull null
                }
                StoriesList.StoriesItem(
                    id = story.id,
                    title = story.title,
                    titleTranslated = translation.title,
                    featuredImage = featuredImage,
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
        val translationPart = translation.parts.firstOrNull { it.id == startPartId }
        if (learningPart == null || translationPart == null) {
            if (learningPart == null) {
                Timber.e("Story $id is missing part with id $startPartId")
            }
            if (translationPart == null) {
                Timber.e("Story $id translation is missing part with id $startPartId")
            }
            return null
        }

        var hasMismatch = false
        if (learningPart.content.size != translationPart.content.size) {
            Timber.e(
                "Story $id content could not be fully matched between $learningLanguage and $translationsLanguage"
            )
            hasMismatch = true
        }

        val storyElements = mutableListOf<StoryElement>()
        if (!hasMismatch) {
            val zippedContent = learningPart.content.zip(translationPart.content)
            loop@ for ((storyElementData, translationElement) in zippedContent) {
                val storyElement = storyElementData.toStoryElement(
                    storyId = id,
                    translation = translationElement,
                    learningLanguage = learningLanguage,
                    translationsLanguage = translationsLanguage,
                )
                if (storyElement == null) {
                    hasMismatch = true
                    break@loop
                }
                storyElements += storyElement
            }
        }

        return if (hasMismatch) {
            null
        } else {
            Story(
                id = id,
                title = title,
                translatedTitle = translation.title,
                content = storyElements,
                currentStoryElementIndex = position,
            )
        }
    }

    private suspend fun StoryElementData.toStoryElement(
        storyId: String,
        translation: StoryElementData,
        learningLanguage: String,
        translationsLanguage: String,
    ): StoryElement? {
        return when (this) {
            is StoryElementData.ParagraphData -> {
                val translationParagraph = translation as? StoryElementData.ParagraphData
                if (translationParagraph == null) {
                    Timber.e("No matching translation found for paragraph in story $storyId")
                    null
                } else if (sentences.size != translationParagraph.sentences.size) {
                    Timber.e(
                        "Mismatched number of sentences in story $storyId for languages " +
                                "$learningLanguage and $translationsLanguage"
                    )
                    null
                } else {
                    StoryElement.Paragraph(
                        sentences = sentences,
                        sentencesTranslations = translationParagraph.sentences
                    )
                }
            }

            is StoryElementData.ImageData -> {
                val bitmap = storiesLocalDataSource.loadStoryImage(
                    storyId = storyId,
                    path = path,
                )
                if (bitmap == null) {
                    Timber.e("Image $path missing for story $storyId")
                    null
                } else {
                    StoryElement.Image(
                        contentDescription = contentDescription,
                        bitmap = bitmap,
                    )
                }
            }
        }
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
