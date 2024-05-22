package input.comprehensible.data

import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.model.Story
import javax.inject.Inject

class StoriesTestData @Inject constructor(
    private val storiesLocalDataSource: FakeStoriesLocalDataSource
) {
    fun setLocalStory(
        title: String,
        content: String
    ) {
        storiesLocalDataSource.story = Story(
            title = title,
            content = content
        )
    }

    fun setLocalStories(listOf100Stories: List<TestStory>) {
        TODO("Not yet implemented")
    }
}
