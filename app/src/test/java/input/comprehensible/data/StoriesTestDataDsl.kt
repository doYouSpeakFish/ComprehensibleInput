package input.comprehensible.data

import input.comprehensible.data.sources.FakeStoriesLocalDataSource
import input.comprehensible.data.stories.model.Story
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
