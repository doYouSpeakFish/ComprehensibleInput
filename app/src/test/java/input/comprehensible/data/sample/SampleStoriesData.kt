package input.comprehensible.data.sample

object SampleStoriesData {
    val listOf100Stories = List(100) { storyNumber ->
        TestStory(
            id = "$storyNumber",
            germanTitle = "German Title $storyNumber",
            englishTitle = "English Title $storyNumber",
            content = List(10) { paragraphNumber ->
                if (paragraphNumber % 2 == 0) {
                    TestStoryPart.Image(contentDescription = "Image $storyNumber-$paragraphNumber")
                } else {
                    TestStoryPart.Paragraph(
                        germanText = "German Paragraph $paragraphNumber",
                        englishText = "English Paragraph $paragraphNumber"
                    )
                }
            }
        )
    }
}

data class TestStory(
    val id: String,
    val germanTitle: String,
    val englishTitle: String,
    val content: List<TestStoryPart>,
) {
    val paragraphs: List<TestStoryPart.Paragraph> =
        content.filterIsInstance<TestStoryPart.Paragraph>()

    val images: List<TestStoryPart.Image> =
        content.filterIsInstance<TestStoryPart.Image>()
}

sealed interface TestStoryPart {
    data class Paragraph(
        val germanText: String,
        val englishText: String,
    ) : TestStoryPart

    data class Image(val contentDescription: String) : TestStoryPart
}
