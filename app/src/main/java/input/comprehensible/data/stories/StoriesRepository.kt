package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryChoice
import input.comprehensible.data.stories.model.StoryElement
import input.comprehensible.data.stories.model.StoryPart
import input.comprehensible.data.stories.sources.stories.local.StoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryChoiceData
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.data.stories.sources.stories.local.StoryPartData
import input.comprehensible.data.stories.sources.storyinfo.local.StoriesInfoLocalDataSource
import input.comprehensible.data.stories.sources.storyinfo.local.getOrCreateStory
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import input.comprehensible.util.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * A repository for stories.
 */
@Suppress("TooManyFunctions")
class StoriesRepository(
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
                translation = translatedStoryData,
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
                storyInfo = storyInfo,
            )
            StoryResult.Success(story)
        }.catch { throwable ->
            Timber.e(
                throwable,
                "Failed to load story %s for learning language %s and translations language %s",
                id,
                learningLanguage,
                translationsLanguage,
            )
            emit(StoryResult.Error)
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

    suspend fun updateStoryChoice(id: String, partId: String) {
        storiesInfoLocalDataSource.updateStoryChoice(
            id = id,
            lastChosenPartId = partId,
        )
    }

    private fun getStoriesSorted(
        language: String
    ): Flow<List<StoryData>> = storiesLocalDataSource
        .getStories(learningLanguage = language)
        .map { stories ->
            stories
                .sortedByDescending { it.id }
                .sortedByDescending { it.id.length }
        }

    private fun getTranslations(
        language: String
    ): Flow<Map<String, StoryData>> = storiesLocalDataSource
        .getStories(learningLanguage = language)
        .map { stories ->
            stories.associateBy { it.id }
        }

    private suspend fun StoryData.toStory(
        translation: StoryData,
        learningLanguage: String,
        translationsLanguage: String,
        storyInfo: StoryEntity,
    ): Story {
        val path = getPartIdsForCurrentPathThroughStory(
            lastChosenPartId = storyInfo.lastChosenPartId,
        )

        val parts = path.mapIndexed { i, partId ->
            val learningPart = requireNotNull(partsById[partId]) {
                "Story $id is missing part with id $partId"
            }
            val translationPart = requireNotNull(translation.partsById[partId]) {
                "Story $id translation is missing part with id $partId"
            }
            val nextPartId = path.getOrNull(i + 1)
            learningPart.toStoryPart(
                storyId = id,
                translation = translationPart,
                chosenChoiceId = nextPartId,
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
                children = childrenByParentId[partId].orEmpty(),
                translatedChildren = translation.childrenByParentId[partId].orEmpty(),
            )
        }

        return Story(
            id = id,
            title = title,
            translatedTitle = translation.title,
            parts = parts,
            currentPartId = storyInfo.partId ?: startPartId,
            storyPosition = storyInfo.position
        )
    }

    private fun StoryData.getPartIdsForCurrentPathThroughStory(
        lastChosenPartId: String?,
    ): List<String> {
        if (lastChosenPartId == null) return listOf(startPartId)
        return buildList {
            var nextPartId: String? = lastChosenPartId
            while (nextPartId != null) {
                add(nextPartId)
                nextPartId = partsById[nextPartId]?.choice?.parentPartId
            }
        }.reversed()
    }

    @Suppress("LongParameterList")
    private suspend fun StoryPartData.toStoryPart(
        storyId: String,
        translation: StoryPartData,
        chosenChoiceId: String?,
        learningLanguage: String,
        translationsLanguage: String,
        children: List<StoryPartData>,
        translatedChildren: List<StoryPartData>,
    ): StoryPart {
        val translatedChildrenById = translatedChildren.associateBy { it.id }
        val choicesWithTranslations = children.map { childPart ->
            val translatedChild = requireNotNull(translatedChildrenById[childPart.id]) {
                "Story $id translation is missing part with id ${childPart.id}"
            }
            val choice = requireNotNull(childPart.choice) {
                "Story $id child part ${childPart.id} is missing its parent choice"
            }
            val translatedChoice = requireNotNull(translatedChild.choice) {
                "Story $id translation is missing parent choice for part ${childPart.id}"
            }
            choice.toStoryChoice(
                translation = translatedChoice,
                targetPartId = childPart.id,
                isChosen = childPart.id == chosenChoiceId,
            )
        }

        require(content.size == translation.content.size) {
            "Story $id content could not be fully matched between $learningLanguage and $translationsLanguage"
        }
        val elements = content.zip(translation.content) { elementData, translation ->
            val element = elementData.toStoryElement(
                storyId = storyId,
                translation = translation,
                learningLanguage = learningLanguage,
                translationsLanguage = translationsLanguage,
            )
            requireNotNull(element) {
                "Story $id content could not be fully matched between $learningLanguage and $translationsLanguage for part $id"
            }
        }

        return StoryPart(
            id = id,
            elements = elements,
            choices = choicesWithTranslations,
        )
    }

    private fun StoryChoiceData.toStoryChoice(
        translation: StoryChoiceData,
        targetPartId: String,
        isChosen: Boolean,
    ): StoryChoice {
        return StoryChoice(
            text = text,
            translatedText = translation.text,
            targetPartId = targetPartId,
            isChosen = isChosen,
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

    companion object : Singleton<StoriesRepository>() {
        override fun create() = StoriesRepository(
            storiesLocalDataSource = StoriesLocalDataSource(),
            storiesInfoLocalDataSource = StoriesInfoLocalDataSource(),
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
