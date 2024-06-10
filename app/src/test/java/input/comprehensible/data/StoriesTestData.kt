package input.comprehensible.data

import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sample.TestStoryPart
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.model.Story
import input.comprehensible.data.stories.model.StoryElement
import javax.inject.Inject

class StoriesTestData @Inject constructor(
    private val storiesLocalDataSource: FakeStoriesLocalDataSource
) {
    fun setLocalStories(stories: List<TestStory>) {
        storiesLocalDataSource.stories = stories.map { testStory ->
            Story(
                id = testStory.id,
                title = testStory.title,
                content = testStory.content
                    .filterIsInstance<TestStoryPart.Paragraph>()
                    .map { part ->
                        StoryElement.Paragraph(part.text)
                    }
            )
        }
    }
}
