package input.comprehensible.data.sample

object SampleStoriesData {
    val listOf100Stories = List(100) { storyNumber ->
        TestStory(
            id = "$storyNumber",
            title = "Title $storyNumber",
            content = List(10) { paragraphNumber ->
                TestStoryPart.Paragraph(text = "Paragraph $paragraphNumber")
            }
        )
    }
}

data class TestStory(
    val id: String,
    val title: String,
    val content: List<TestStoryPart>,
) {
    val paragraphs: List<TestStoryPart.Paragraph> =
        content.filterIsInstance<TestStoryPart.Paragraph>()
}

sealed interface TestStoryPart {
    data class Paragraph(val text: String) : TestStoryPart
}
