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
                if (language == languageCode) {
                    storyData.map { storyDataItem ->
                        if (storyDataItem.id != story.id) {
                            return@map storyDataItem
                        }

                        val firstPartIndex = storyDataItem.parts.indexOfFirst { part ->
                            part.content.any { it is StoryElementData.ParagraphData }
                        }
                        if (firstPartIndex == -1) {
                            return@map storyDataItem
                        }
                        val firstParagraphIndex = storyDataItem.parts[firstPartIndex].content.indexOfFirst {
                            it is StoryElementData.ParagraphData
                        }
                        if (firstParagraphIndex == -1) {
                            return@map storyDataItem
                        }

                        storyDataItem.copy(
                            parts = storyDataItem.parts.mapIndexed { index, part ->
                                if (index != firstPartIndex) {
                                    return@mapIndexed part
                                }
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
                } else {
                    storyData
                }
            }
    }

    private fun TestStory.toStoryData(
        title: String,
        languageCode: String,
    ): StoryData {
        val partData = parts.map { part ->
            part.toStoryPartData(languageCode = languageCode)
        }
        val featuredImagePath = partData
            .flatMap { it.content }
            .filterIsInstance<StoryElementData.ImageData>()
            .firstOrNull()
            ?.path
            ?: ""
        return StoryData(
            id = id,
            title = title,
            startPartId = parts.firstOrNull()?.id ?: "",
            featuredImagePath = featuredImagePath,
            parts = partData,
        )
    }

    private fun TestStoryPartSegment.toStoryPartData(
        languageCode: String,
    ): StoryPartData {
        val contentData = content.map { part ->
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
        return StoryPartData(
            id = id,
            content = contentData,
            choices = choices.map { it.toStoryChoiceData(languageCode = languageCode) },
        )
    }

    private fun TestStoryPart.Paragraph.sentencesFor(languageCode: String): List<String> = when (languageCode) {
        "de" -> germanSentences
        "es" -> spanishSentences
        "en" -> englishSentences
        else -> englishSentences
    }

    private fun TestStoryChoice.toStoryChoiceData(languageCode: String): StoryChoiceData {
        val text = textByLanguage[languageCode]
            ?: textByLanguage["en"]
            ?: error("Missing choice text for language $languageCode")
        return StoryChoiceData(
            text = text,
            targetPartId = targetPartId,
        )
    }
}
