package input.comprehensible.data.sample

object SampleStoriesData {
    val listOf100Stories = List(100) {
        TestStory(
            title = "Title $it",
            content = "Content $it"
        )
    }
}

data class TestStory(
    val title: String,
    val content: String,
)
