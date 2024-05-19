package `in`.comprehensible.data

import `in`.comprehensible.data.sources.FakeStoriesLocalDataSource
import `in`.comprehensible.data.stories.model.Story
import javax.inject.Inject

class StoriesTestDataDsl @Inject constructor(
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
}
