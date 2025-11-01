package input.comprehensible.data

import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sample.TestStoryPart
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import input.comprehensible.data.stories.sources.stories.local.StoryPartData
import javax.inject.Inject

class StoriesTestData @Inject constructor(
    private val storiesLocalDataSource: FakeStoriesLocalDataSource
) {
    fun setLocalStories(stories: List<TestStory>) {
        val germanStories = stories.map { it.toStoryData(it.germanTitle) { part -> part.germanSentences } }
        val englishStories = stories.map { it.toStoryData(it.englishTitle) { part -> part.englishSentences } }
        val spanishStories = stories.map { it.toStoryData(it.spanishTitle) { part -> part.spanishSentences } }
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

                        val singlePart = storyDataItem.parts.firstOrNull()
                            ?: return@map storyDataItem
                        val firstParagraphIndex = singlePart.content.indexOfFirst {
                            it is StoryElementData.ParagraphData
                        }
                        if (firstParagraphIndex == -1) {
                            return@map storyDataItem
                        }

                        storyDataItem.copy(
                            parts = listOf(
                                singlePart.copy(
                                    content = singlePart.content.mapIndexed { index, element ->
                                        if (index != firstParagraphIndex) {
                                            return@mapIndexed element
                                        }

                                        val paragraph = element as StoryElementData.ParagraphData
                                        paragraph.copy(
                                            sentences = paragraph.sentences.dropLast(1)
                                        )
                                    }
                                )
                            )
                        )
                    }
                } else {
                    storyData
                }
            }
    }

    private fun TestStory.toStoryData(
        title: String,
        paragraphSentences: (TestStoryPart.Paragraph) -> List<String>,
    ): StoryData {
        val singlePartId = "main"
        val partContent = content.map { part ->
            when (part) {
                is TestStoryPart.Image -> StoryElementData.ImageData(
                    contentDescription = part.contentDescription,
                    path = "",
                )

                is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                    sentences = paragraphSentences(part),
                )
            }
        }
        val featuredImagePath = partContent
            .filterIsInstance<StoryElementData.ImageData>()
            .firstOrNull()
            ?.path
            ?: ""
        return StoryData(
            id = id,
            title = title,
            startPartId = singlePartId,
            featuredImagePath = featuredImagePath,
            parts = listOf(
                StoryPartData(
                    id = singlePartId,
                    content = partContent,
                )
            ),
        )
    }
}
