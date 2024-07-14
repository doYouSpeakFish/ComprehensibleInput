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
                                text = part.germanText,
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
                                text = part.englishText,
                            )
                        }
                    }
            )
        }
        storiesLocalDataSource.stories = mapOf(
            "de" to germanStories,
            "en" to englishStories
        )
    }
}
