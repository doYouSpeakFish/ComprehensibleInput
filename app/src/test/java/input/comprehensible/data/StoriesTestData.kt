package input.comprehensible.data

import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.model.Story
import javax.inject.Inject

class StoriesTestData @Inject constructor(
    private val storiesLocalDataSource: FakeStoriesLocalDataSource
) {
    fun setLocalStories(stories: List<TestStory>) {
        storiesLocalDataSource.stories = stories.map {
            Story(
                id = it.id,
                title = it.title,
                content = it.content
            )
        }
    }
}
