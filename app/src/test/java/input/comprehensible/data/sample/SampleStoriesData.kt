package input.comprehensible.data.sample

object SampleStoriesData {
    val listOf100Stories = List(100) {
        TestStory(
            id = "$it",
            title = "Title $it",
            content = "Content $it"
        )
    }
}

data class TestStory(
    val id: String,
    val title: String,
    val content: String,
)
