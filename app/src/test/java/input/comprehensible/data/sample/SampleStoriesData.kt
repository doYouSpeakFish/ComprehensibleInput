package input.comprehensible.data.sample

object SampleStoriesData {
    val listOf100Stories = List(100) { storyNumber ->
        TestStory(
            id = "$storyNumber",
            germanTitle = "German Title $storyNumber",
            englishTitle = "English Title $storyNumber",
            spanishTitle = "Spanish Title $storyNumber",
            content = List(10) { paragraphNumber ->
                if (paragraphNumber % 2 == 0) {
                    TestStoryPart.Image(contentDescription = "Image $storyNumber-$paragraphNumber")
                } else {
                    TestStoryPart.Paragraph(
                        germanSentences = List(10) { sentenceNumber ->
                            "German Paragraph $paragraphNumber Sentence $sentenceNumber"
                        },
                        englishSentences = List(10) { sentenceNumber ->
                            "English Paragraph $paragraphNumber Sentence $sentenceNumber"
                        },
                        spanishSentences = List(10) { sentenceNumber ->
                            "Spanish Paragraph $paragraphNumber Sentence $sentenceNumber"
                        }
                    )
                }
            }
        )
    }.reversed()

    val aiGeneratedStory = TestStory(
        id = "ai",
        germanTitle = "AI-Generated German Title",
        englishTitle = "AI-Generated English Title",
        spanishTitle = "AI-Generated Spanish Title",
        content = List(10) { paragraphNumber ->
            if (paragraphNumber % 2 == 0) {
                TestStoryPart.Image(contentDescription = "AI-Generated Image $paragraphNumber")
            } else {
                TestStoryPart.Paragraph(
                    germanSentences = List(10) { sentenceNumber ->
                        "AI-Generated German Paragraph $paragraphNumber Sentence $sentenceNumber"
                    },
                    englishSentences = List(10) { sentenceNumber ->
                        "AI-Generated English Paragraph $paragraphNumber Sentence $sentenceNumber"
                    },
                    spanishSentences = List(10) { sentenceNumber ->
                        "AI-Generated Spanish Paragraph $paragraphNumber Sentence $sentenceNumber"
                    }
                )
            }
        }
    )
}

data class TestStory(
    val id: String,
    val germanTitle: String,
    val englishTitle: String,
    val spanishTitle: String,
    val content: List<TestStoryPart>,
) {
    val paragraphs: List<TestStoryPart.Paragraph> =
        content.filterIsInstance<TestStoryPart.Paragraph>()

    val images: List<TestStoryPart.Image> =
        content.filterIsInstance<TestStoryPart.Image>()
}

sealed interface TestStoryPart {
    data class Paragraph(
        val germanSentences: List<String>,
        val englishSentences: List<String>,
        val spanishSentences: List<String>,
    ) : TestStoryPart

    data class Image(val contentDescription: String) : TestStoryPart
}
