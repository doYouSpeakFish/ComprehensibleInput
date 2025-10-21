package input.comprehensible.data

import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sample.TestStoryPart
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.sources.stories.local.StoryData
import input.comprehensible.data.stories.sources.stories.local.StoryElementData
import javax.inject.Inject

class StoriesTestData @Inject constructor(
    private val storiesLocalDataSource: FakeStoriesLocalDataSource
) {
    fun setLocalStories(stories: List<TestStory>) {
        val germanStories = stories.map { testStory ->
            StoryData(
                id = testStory.id,
                title = testStory.germanTitle,
                content = testStory.content
                    .map { part ->
                        when (part) {
                            is TestStoryPart.Image -> StoryElementData.ImageData(
                                contentDescription = part.contentDescription,
                                path = ""
                            )

                            is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                                sentences = part.germanSentences,
                            )
                        }
                    }
            )
        }
        val englishStories = stories.map { testStory ->
            StoryData(
                id = testStory.id,
                title = testStory.englishTitle,
                content = testStory.content
                    .map { part ->
                        when (part) {
                            is TestStoryPart.Image -> StoryElementData.ImageData(
                                contentDescription = part.contentDescription,
                                path = ""
                            )

                            is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                                sentences = part.englishSentences,
                            )
                        }
                    }
            )
        }
        val spanishStories = stories.map { testStory ->
            StoryData(
                id = testStory.id,
                title = testStory.spanishTitle,
                content = testStory.content
                    .map { part ->
                        when (part) {
                            is TestStoryPart.Image -> StoryElementData.ImageData(
                                contentDescription = part.contentDescription,
                                path = ""
                            )

                            is TestStoryPart.Paragraph -> StoryElementData.ParagraphData(
                                sentences = part.spanishSentences,
                            )
                        }
                    }
            )
        }
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

    fun resetStoryLoadDelay() {
        storiesLocalDataSource.storyLoadDelayMillis = 0L
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

                        val firstParagraphIndex = storyDataItem.content.indexOfFirst {
                            it is StoryElementData.ParagraphData
                        }
                        if (firstParagraphIndex == -1) {
                            return@map storyDataItem
                        }

                        storyDataItem.copy(
                            content = storyDataItem.content.mapIndexed { index, element ->
                                if (index != firstParagraphIndex) {
                                    return@mapIndexed element
                                }

                                val paragraph = element as StoryElementData.ParagraphData
                                paragraph.copy(
                                    sentences = paragraph.sentences
                                        .dropLast(1)
                                        .ifEmpty { emptyList() }
                                )
                            }
                        )
                    }
                } else {
                    storyData
                }
            }
    }
}
