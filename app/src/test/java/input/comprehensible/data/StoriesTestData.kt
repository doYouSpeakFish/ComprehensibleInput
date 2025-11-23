package input.comprehensible.data

import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sample.TestStoryPart
import input.comprehensible.data.sample.TestStoryPartSegment
import input.comprehensible.data.sample.TestStoryChoice
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.data.stories.sources.stories.local.StoryPartData
import input.comprehensible.data.stories.sources.stories.local.StoryChoiceData
import timber.log.Timber
import javax.inject.Inject

class StoriesTestData @Inject constructor(
    private val storiesLocalDataSource: FakeStoriesLocalDataSource
) {
    fun setLocalStories(stories: List<TestStory>) {
        val germanStories = stories.map { it.toStoryData(title = it.germanTitle, languageCode = "de") }
        val englishStories = stories.map { it.toStoryData(title = it.englishTitle, languageCode = "en") }
        val spanishStories = stories.map { it.toStoryData(title = it.spanishTitle, languageCode = "es") }
        storiesLocalDataSource.stories = mapOf(
            "de" to germanStories,
            "en" to englishStories,
            "es" to spanishStories,
        )
    }

    fun hideTranslationForStory(languageCode: String, story: TestStory) {
        removeStoryForLanguage(languageCode = languageCode, story = story)
    }

    fun hideStoryForLanguage(languageCode: String, story: TestStory) {
        removeStoryForLanguage(languageCode = languageCode, story = story)
    }

    fun delayStoryLoads(delayMillis: Long) {
        storiesLocalDataSource.storyLoadDelayMillis = delayMillis
    }

    private fun removeStoryForLanguage(languageCode: String, story: TestStory) {
        storiesLocalDataSource.stories = storiesLocalDataSource.stories
            .mapValues { (language, storyData) ->
                if (language == languageCode) {
                    storyData.filterNot { it.id == story.id }
                } else {
                    storyData
                }
            }
    }

    fun mismatchTranslationForStory(languageCode: String, story: TestStory) {
        storiesLocalDataSource.stories = storiesLocalDataSource.stories
            .mapValues { (language, storyData) ->
                if (language != languageCode) return@mapValues storyData

                storyData.map { storyDataItem ->
                    if (storyDataItem.id != story.id) {
                        return@map storyDataItem
                    }

                    val firstParagraphTarget = storyDataItem.start
                        .flattenParts()
                        .firstOrNull { part -> part.content.any { it is StoryElementData.ParagraphData } }
                        ?: return@map storyDataItem

                    val firstParagraphIndex = firstParagraphTarget.content.indexOfFirst {
                        it is StoryElementData.ParagraphData
                    }
                    if (firstParagraphIndex == -1) {
                        return@map storyDataItem
                    }

                    storyDataItem.copy(
                        start = storyDataItem.start.updatePart(firstParagraphTarget.id) { part ->
                            part.copy(
                                content = part.content.mapIndexed { contentIndex, element ->
                                    if (contentIndex != firstParagraphIndex) {
                                        return@mapIndexed element
                                    }

                                    val paragraph = element as StoryElementData.ParagraphData
                                    paragraph.copy(
                                        sentences = paragraph.sentences.dropLast(1)
                                    )
                                }
                            )
                        }
                    )
                }
            }
    }

    private fun TestStory.toStoryData(
        title: String,
        languageCode: String,
    ): StoryData {
        val partDataById = mutableMapOf<String, StoryPartData>()

        fun buildPart(partId: String): StoryPartData {
            partDataById[partId]?.let { return it }

            val partSegment = parts.firstOrNull { it.id == partId }
                ?: error("Missing part with id $partId")
            val contentData = partSegment.content.map { part ->
                when (part) {
                    is TestStoryPart.Image -> StoryElementData.ImageData(
                        contentDescription = part.contentDescription,
                        path = "",
                    )

                    is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                        sentences = part.sentencesFor(languageCode),
                    )
                }
            }
            val partData = StoryPartData(
                id = partSegment.id,
                content = contentData,
                choices = partSegment.choices.map { it.toStoryChoiceData(languageCode = languageCode, buildPart = ::buildPart) },
            )
            partDataById[partId] = partData
            return partData
        }

        val startPart = parts.firstOrNull()?.id?.let(::buildPart) ?: StoryPartData(
            id = "",
            content = emptyList(),
        )

        val featuredImagePath = partDataById.values
            .flatMap { it.content }
            .filterIsInstance<StoryElementData.ImageData>()
            .firstOrNull()
            ?.path
            ?: ""
        return StoryData(
            id = id,
            title = title,
            featuredImagePath = featuredImagePath,
            start = startPart,
        )
    }

    private fun TestStoryPart.Paragraph.sentencesFor(languageCode: String): List<String> = when (languageCode) {
        "de" -> germanSentences
        "es" -> spanishSentences
        "en" -> englishSentences
        else -> englishSentences
    }

    private fun TestStoryChoice.toStoryChoiceData(
        languageCode: String,
        buildPart: (String) -> StoryPartData,
    ): StoryChoiceData {
        val text = textByLanguage[languageCode]
            ?: textByLanguage["en"]
            ?: error("Missing choice text for language $languageCode")
        return StoryChoiceData(
            text = text,
            part = runCatching { buildPart(targetPartId) }
                .getOrElse {
                    Timber.e(it, "Failed to build choice part for %s", targetPartId)
                    buildPart(targetPartId)
                },
        )
    }

    private fun StoryPartData.flattenParts(): List<StoryPartData> {
        val parts = mutableListOf<StoryPartData>()

        fun collect(part: StoryPartData) {
            parts += part
            part.choices.forEach { collect(it.part) }
        }

        collect(this)
        return parts
    }

    private fun StoryPartData.updatePart(id: String, transform: (StoryPartData) -> StoryPartData): StoryPartData {
        if (this.id == id) {
            return transform(this)
        }

        return copy(
            choices = choices.map { choice ->
                choice.copy(part = choice.part.updatePart(id, transform))
            }
        )
    }
}
