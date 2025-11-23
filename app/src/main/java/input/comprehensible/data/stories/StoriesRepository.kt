package input.comprehensible.data.stories

import input.comprehensible.data.stories.model.StoriesList
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryChoice
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
@Suppress("TooManyFunctions")
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

    suspend fun updateStoryChoice(id: String, partId: String) {
        storiesInfoLocalDataSource.updateStory(
            id = id,
            partId = partId,
            lastChosenPartId = partId,
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
        val currentPartId = storyInfo.partId ?: start.id
        val furthestPartId = storyInfo.lastChosenPartId ?: currentPartId

        val translationPartsById = translation.start.collectParts()

        if (!translation.start.hasPathTo(furthestPartId)) {
            Timber.e(
                "Translation %s does not have a path to part %s in story %s",
                translationsLanguage,
                furthestPartId,
                id,
            )
            return null
        }

        val path = start.pathTo(furthestPartId) ?: run {
            Timber.e("Story %s is missing part with id %s", id, furthestPartId)
            return null
        }

        val context = StoryConversionContext(
            storyId = id,
            languageLabel = "$learningLanguage/$translationsLanguage",
            translationPartsById = translationPartsById,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )

        val storyParts = buildStoryParts(
            path = path,
            context = context,
        ) ?: return null

        val resolvedCurrentPartId = path.firstOrNull { it.id == currentPartId }?.id ?: path.first().id

        return Story(
            id = id,
            title = title,
            translatedTitle = translation.title,
            parts = storyParts,
            currentPartId = resolvedCurrentPartId,
            storyPosition = storyInfo.position,
        )
    }

    private suspend fun buildStoryParts(
        path: List<StoryPartData>,
        context: StoryConversionContext,
    ): List<StoryPart>? {
        val choiceOptionsByPartId = mutableMapOf<String, List<StoryChoiceOption>>()

        fun optionsForPart(learningPart: StoryPartData): List<StoryChoiceOption>? {
            val translationPart = context.translationPartsById[learningPart.id] ?: run {
                Timber.e("Story %s translation is missing part with id %s", context.storyId, learningPart.id)
                return null
            }

            val cachedOptions = choiceOptionsByPartId[learningPart.id]
            if (cachedOptions != null) return cachedOptions

            val options = buildChoiceOptions(
                context = StoryChoiceContext(
                    storyId = context.storyId,
                    partId = learningPart.id,
                    languageLabel = context.languageLabel,
                ),
                learningPart = learningPart,
                translationPart = translationPart,
            ) ?: return null

            choiceOptionsByPartId[learningPart.id] = options
            return options
        }

        fun leadingChoiceFor(index: Int, currentPart: StoryPartData): LeadingChoiceOutcome {
            if (index == 0) return LeadingChoiceOutcome(null, true)
            val parentPart = path[index - 1]
            val parentOptions = optionsForPart(parentPart) ?: return LeadingChoiceOutcome(null, false)
            val choice = parentOptions.firstOrNull { option -> option.targetPartId == currentPart.id } ?: run {
                Timber.e(
                    "Story %s part %s is missing choice leading to part %s",
                    context.storyId,
                    parentPart.id,
                    currentPart.id,
                )
                return LeadingChoiceOutcome(null, false)
            }
            return LeadingChoiceOutcome(choice, true)
        }

        val storyParts = mutableListOf<StoryPart>()

        for ((index, learningPart) in path.withIndex()) {
            val storyPart = buildStoryPart(
                index = index,
                path = path,
                learningPart = learningPart,
                context = context,
                optionsForPart = ::optionsForPart,
            ) ?: return null

            storyParts += storyPart
        }

        return storyParts
    }

    private suspend fun buildStoryElements(
        learningPart: StoryPartData,
        translationPart: StoryPartData,
        context: StoryConversionContext,
    ): List<StoryElement>? {
        if (learningPart.content.size != translationPart.content.size) {
            Timber.e(
                "Story %s content could not be fully matched between %s and %s for part %s",
                context.storyId,
                context.learningLanguage,
                context.translationsLanguage,
                learningPart.id,
            )
            return null
        }

        return learningPart.content
            .zip(translationPart.content)
            .map { (storyElementData, translationElement) ->
                storyElementData.toStoryElement(
                    storyId = context.storyId,
                    translation = translationElement,
                    learningLanguage = context.learningLanguage,
                    translationsLanguage = context.translationsLanguage,
                ) ?: return null
            }
    }

    private suspend fun buildStoryPart(
        index: Int,
        path: List<StoryPartData>,
        learningPart: StoryPartData,
        context: StoryConversionContext,
        optionsForPart: (StoryPartData) -> List<StoryChoiceOption>?,
    ): StoryPart? {
        val translationPart = context.translationPartsById[learningPart.id] ?: run {
            Timber.e("Story %s translation is missing part with id %s", context.storyId, learningPart.id)
            return null
        }

        val elements = buildStoryElements(
            learningPart = learningPart,
            translationPart = translationPart,
            context = context,
        ) ?: return null

        val choiceOptions = optionsForPart(learningPart) ?: return null
        val choice = if (choiceOptions.isEmpty()) {
            null
        } else {
            StoryChoice.Available(choiceOptions)
        }

        val leadingChoiceOutcome = findLeadingChoice(
            index = index,
            path = path,
            currentPartId = learningPart.id,
            optionsForPart = optionsForPart,
            storyId = context.storyId,
        )
        if (!leadingChoiceOutcome.isValid) return null

        return StoryPart(
            id = learningPart.id,
            leadingChoice = leadingChoiceOutcome.choice,
            elements = elements,
            choice = choice,
        )
    }

    private fun findLeadingChoice(
        index: Int,
        path: List<StoryPartData>,
        currentPartId: String,
        optionsForPart: (StoryPartData) -> List<StoryChoiceOption>?,
        storyId: String,
    ): LeadingChoiceOutcome {
        if (index == 0) return LeadingChoiceOutcome(null, true)
        val parentPart = path[index - 1]
        val parentOptions = optionsForPart(parentPart) ?: return LeadingChoiceOutcome(null, false)
        val choice = parentOptions.firstOrNull { option -> option.targetPartId == currentPartId } ?: run {
            Timber.e(
                "Story %s part %s is missing choice leading to part %s",
                storyId,
                parentPart.id,
                currentPartId,
            )
            return LeadingChoiceOutcome(null, false)
        }
        return LeadingChoiceOutcome(choice, true)
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

private fun buildChoiceOptions(
    context: StoryChoiceContext,
    learningPart: StoryPartData,
    translationPart: StoryPartData,
): List<StoryChoiceOption>? {
    if (translationPart.choices.size != learningPart.choices.size) {
        Timber.e(
            "Mismatched number of choices in story %s (%s) in part %s",
            context.storyId,
            context.languageLabel,
            context.partId,
        )
        return null
    }
    val translationChoicesByTarget = translationPart.choices.associateBy { it.part.id }
    val options = mutableListOf<StoryChoiceOption>()
    learningPart.choices.forEach { choice ->
        val targetPartId = choice.part.id
        val translationChoice = translationChoicesByTarget[targetPartId] ?: run {
            Timber.e("No matching translation found for choice in story %s part %s", context.storyId, context.partId)
            return null
        }
        options += StoryChoiceOption(
            text = choice.text,
            translatedText = translationChoice.text,
            targetPartId = targetPartId,
        )
    }

    return options
}

private fun StoryPartData.collectParts(): Map<String, StoryPartData> {
    val partsById = mutableMapOf<String, StoryPartData>()

    fun collect(part: StoryPartData) {
        if (partsById.containsKey(part.id)) {
            Timber.e("Duplicate part id %s detected in story", part.id)
            return
        }
        partsById[part.id] = part
        part.choices.forEach { choice -> collect(choice.part) }
    }

    collect(this)
    return partsById
}

private fun StoryPartData.pathTo(targetId: String): List<StoryPartData>? {
    if (id == targetId) return listOf(this)

    choices.forEach { choice ->
        val path = choice.part.pathTo(targetId)
        if (path != null) return listOf(this) + path
    }

    return null
}

private fun StoryPartData.hasPathTo(targetId: String): Boolean = pathTo(targetId) != null

private data class StoryChoiceContext(
    val storyId: String,
    val partId: String,
    val languageLabel: String,
)

private data class StoryConversionContext(
    val storyId: String,
    val languageLabel: String,
    val translationPartsById: Map<String, StoryPartData>,
    val learningLanguage: String,
    val translationsLanguage: String,
)

private data class LeadingChoiceOutcome(
    val choice: StoryChoiceOption?,
    val isValid: Boolean,
)
