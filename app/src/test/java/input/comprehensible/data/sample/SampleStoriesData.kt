package input.comprehensible.data.sample

object SampleStoriesData {
    val listOf100Stories = List(100) { storyNumber ->
        TestStory(
            id = "$storyNumber",
            title = "Title $storyNumber",
            content = List(10) { paragraphNumber ->
                if (paragraphNumber % 2 == 0) {
                    TestStoryPart.Image(contentDescription = "Image $paragraphNumber")
                } else {
                    TestStoryPart.Paragraph(text = "Paragraph $paragraphNumber")
                }
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

    val images: List<TestStoryPart.Image> =
        content.filterIsInstance<TestStoryPart.Image>()
}

sealed interface TestStoryPart {
    data class Paragraph(val text: String) : TestStoryPart

    data class Image(val contentDescription: String) : TestStoryPart
}
