package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.StoriesListResult
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    fun storiesList(
        learningLanguage: String,
        translationsLanguage: String,
    ): Flow<StoriesListResult> = flow {
        runCatching {
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
            StoriesList(
                stories = storiesWithTranslations.mapNotNull { (story, translation) ->
                    val featuredImage = storiesLocalDataSource.loadStoryImage(
                        storyId = story.id,
                        path = story.featuredImagePath,
                    ) ?: run {
                        Timber.e(
                            "Failed to load featured image for story %s using path %s",
                            story.id,
                            story.featuredImagePath,
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
        }.fold(
            onSuccess = { storiesList ->
                emit(StoriesListResult.Success(storiesList))
            },
            onFailure = { throwable ->
                Timber.e(
                    throwable,
                    "Failed to load stories list for learning language %s and translations language %s",
                    learningLanguage,
                    translationsLanguage,
                )
                emit(StoriesListResult.Error)
            }
        )
    }

    /**
     * Gets a story in the given [learningLanguage] with translations in the given
     * [translationsLanguage].
     */
    fun getStory(
        id: String,
        learningLanguage: String,
        translationsLanguage: String
    ): Flow<StoryResult> = storiesInfoLocalDataSource
        .getStory(id)
        .map { storyInfoEntity ->
            runCatching {
                val storyInfo = storyInfoEntity ?: StoryEntity(id = id, position = 0).also {
                    // First time story opened. Insert info into db so this story can be tracked
                    storiesInfoLocalDataSource.insertStory(story = it)
                }
                val storyData = storiesLocalDataSource.getStory(
                    id = id,
                    language = learningLanguage
                ) ?: run {
                    Timber.e("Story $id not found for language $learningLanguage")
                    return@runCatching StoryResult.Error
                }
                val translatedStoryData = storiesLocalDataSource.getStory(
                    id = id,
                    language = translationsLanguage
                ) ?: run {
                    Timber.e("Translation $translationsLanguage not found for story $id")
                    return@runCatching StoryResult.Error
                }
                val story = storyData.toStory(
                    id = id,
                    translation = translatedStoryData,
                    learningLanguage = learningLanguage,
                    translationsLanguage = translationsLanguage,
                    position = storyInfo.position,
                ) ?: return@runCatching StoryResult.Error
                StoryResult.Success(story)
            }.getOrElse { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                Timber.e(
                    throwable,
                    "Failed to load story %s for learning language %s and translations language %s",
                    id,
                    learningLanguage,
                    translationsLanguage,
                )
                StoryResult.Error
            }
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
        val bitmap = storiesLocalDataSource.loadStoryImage(
            storyId = storyId,
            path = path,
        ) ?: run {
            Timber.e(
                "Failed to load image for story %s at path %s",
                storyId,
                path,
            )
            return null
        }
        return StoryElement.Image(
            contentDescription = contentDescription,
            bitmap = bitmap,
        )
    }

}
sealed interface StoryResult {
    data class Success(val story: Story) : StoryResult
    object Error : StoryResult
}
