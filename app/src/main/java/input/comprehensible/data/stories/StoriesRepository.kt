package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryChoiceOption
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.model.StoryPart
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.data.stories.sources.stories.local.StoryPartData
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.getOrCreateStory
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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

    fun getStories(
        learningLanguage: String,
        translationsLanguage: String,
    ): Flow<StoriesListResult> =
        combine<List<StoryData>, Map<String, StoryData>, StoriesListResult>(
            getStoriesSorted(learningLanguage),
            getTranslations(translationsLanguage),
        ) { stories, translations ->
            val storiesWithTranslations = buildList {
                stories.forEach { story ->
                    val translation = translations[story.id]
                    if (translation != null) add(story to translation)
                }
            }
            val storyListItems = storiesWithTranslations.mapNotNull { (story, translation) ->
                val featuredImage = storiesLocalDataSource.loadStoryImage(
                    storyId = story.id,
                    path = story.featuredImagePath,
                ) ?: run {
                    Timber.e("Failed to load story ${story.id} in list because of missing feature image")
                    return@mapNotNull null
                }
                StoriesList.StoriesItem(
                    id = story.id,
                    title = story.title,
                    titleTranslated = translation.title,
                    featuredImage = featuredImage,
                )
            }
            StoriesListResult.Success(StoriesList(stories = storyListItems))
        }.catch { throwable ->
            Timber.e(
                throwable,
                "Failed to load stories list for learning language %s and translations language %s",
                learningLanguage,
                translationsLanguage,
            )
            emit(StoriesListResult.Error)
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
        .getOrCreateStory(id)
        .map { storyInfo ->
            val storyData = storiesLocalDataSource.getStory(
                id = id,
                language = learningLanguage
            ) ?: run {
                Timber.e("Story $id not found for language $learningLanguage")
                return@map StoryResult.Error
            }
            val translatedStoryData = storiesLocalDataSource.getStory(
                id = id,
                language = translationsLanguage
            ) ?: run {
                Timber.e("Translation $translationsLanguage not found for story $id")
                return@map StoryResult.Error
            }
            val story = storyData.toStory(
                id = id,
                translation = translatedStoryData,
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
                storyInfo = storyInfo,
            ) ?: return@map StoryResult.Error
            StoryResult.Success(story)
        }.catch { throwable ->
            Timber.e(
                throwable,
                "Failed to load story %s for learning language %s and translations language %s",
                id,
                learningLanguage,
                translationsLanguage,
            )
            StoryResult.Error
        }

    suspend fun updateStoryPosition(id: String, storyPosition: Int) {
        storiesInfoLocalDataSource.updateStory(
            id = id,
            position = storyPosition
        )
    }

    suspend fun updateStoryPart(id: String, partId: String) {
        storiesInfoLocalDataSource.updateStory(
            id = id,
            partId = partId
        )
    }

    private fun getStoriesSorted(
        language: String
    ): Flow<List<StoryData>> = storiesLocalDataSource
        .getStories(learningLanguage = language)
        .map { stories ->
            stories.sortedByDescending { it.id }
        }

    private fun getTranslations(
        language: String
    ): Flow<Map<String, StoryData>> = storiesLocalDataSource
        .getStories(learningLanguage = language)
        .map { stories ->
            stories.associateBy { it.id }
        }

    private suspend fun StoryData.toStory(
        id: String,
        translation: StoryData,
        learningLanguage: String,
        translationsLanguage: String,
        storyInfo: StoryEntity,
    ): Story? {
        val learningParts = parts.associateBy { it.id }
        val translationParts = translation.parts.associateBy { it.id }

        val path = buildList {
            var nextPartId: String? = storyInfo.partId
            while (nextPartId != null) {
                add(nextPartId)
                nextPartId = partParents[nextPartId]
            }
            if (!contains(startPartId)) add(startPartId)
        }.reversed()

        val languageLabel = "$learningLanguage/$translationsLanguage"

        val storyParts = mutableListOf<StoryPart>()
        path.forEachIndexed { index, partId ->
            val learningPart = learningParts[partId] ?: run {
                Timber.e("Story %s is missing part with id %s", id, partId)
                return null
            }
            val translationPart = translationParts[partId] ?: run {
                Timber.e("Story %s translation is missing part with id %s", id, partId)
                return null
            }
            if (learningPart.content.size != translationPart.content.size) {
                Timber.e(
                    "Story %s content could not be fully matched between %s and %s for part %s",
                    id,
                    learningLanguage,
                    translationsLanguage,
                    partId,
                )
                return null
            }

            val elements = learningPart.content
                .zip(translationPart.content)
                .map { (storyElementData, translationElement) ->
                    storyElementData.toStoryElement(
                        storyId = id,
                        translation = translationElement,
                        learningLanguage = learningLanguage,
                        translationsLanguage = translationsLanguage,
                    ) ?: return null
                }

            val choiceSelection = if (learningPart.choices.isEmpty()) {
                StoryChoiceSelection(options = emptyList(), chosenOption = null)
            } else {
                val choiceContext = StoryChoiceContext(
                    storyId = id,
                    partId = partId,
                    nextPartId = path.getOrNull(index + 1),
                    languageLabel = languageLabel,
                )
                buildStoryChoice(
                    context = choiceContext,
                    learningPart = learningPart,
                    translationPart = translationPart,
                ) ?: return null
            }

            storyParts += StoryPart(
                id = partId,
                elements = elements,
                options = choiceSelection.options,
                chosenOption = choiceSelection.chosenOption,
            )
        }

        return Story(
            id = id,
            title = title,
            translatedTitle = translation.title,
            parts = storyParts,
            currentPartId = path.last(),
            storyPosition = storyInfo.position,
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

sealed interface StoriesListResult {
    data class Success(val storiesList: StoriesList) : StoriesListResult
    object Error : StoriesListResult
}

sealed interface StoryResult {
    data class Success(val story: Story) : StoryResult
    object Error : StoryResult
}

private fun buildStoryChoice(
    context: StoryChoiceContext,
    learningPart: StoryPartData,
    translationPart: StoryPartData,
): StoryChoiceSelection? {
    if (translationPart.choices.size != learningPart.choices.size) {
        Timber.e(
            "Mismatched number of choices in story %s (%s) in part %s",
            context.storyId,
            context.languageLabel,
            context.partId,
        )
        return null
    }
    val translationChoicesByTarget = translationPart.choices.associateBy { it.targetPartId }
    val options = mutableListOf<StoryChoiceOption>()
    learningPart.choices.forEach { choice ->
        val translationChoice = translationChoicesByTarget[choice.targetPartId] ?: run {
            Timber.e("No matching translation found for choice in story %s part %s", context.storyId, context.partId)
            return null
        }
        options += StoryChoiceOption(
            text = choice.text,
            translatedText = translationChoice.text,
            targetPartId = choice.targetPartId,
        )
    }

    val chosenOption = context.nextPartId?.let { nextPartId ->
        options.firstOrNull { it.targetPartId == nextPartId } ?: run {
            Timber.e(
                "Story %s part %s is missing choice leading to part %s",
                context.storyId,
                context.partId,
                nextPartId,
            )
            return null
        }
    }

    return StoryChoiceSelection(
        options = options,
        chosenOption = chosenOption,
    )
}

private data class StoryChoiceSelection(
    val options: List<StoryChoiceOption>,
    val chosenOption: StoryChoiceOption?,
)

private data class StoryChoiceContext(
    val storyId: String,
    val partId: String,
    val nextPartId: String?,
    val languageLabel: String,
)
