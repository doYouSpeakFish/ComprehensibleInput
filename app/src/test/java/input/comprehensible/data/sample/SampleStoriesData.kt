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

    val importedStory = TestStory(
        id = "imported",
        germanTitle = "German Title Imported",
        englishTitle = "English Title Imported",
        spanishTitle = "Spanish Title Imported",
        content = List(10) { paragraphNumber ->
            if (paragraphNumber % 2 == 0) {
                TestStoryPart.Image(contentDescription = "Image Imported-$paragraphNumber")
            } else {
                TestStoryPart.Paragraph(
                    germanSentences = List(10) { sentenceNumber ->
                        "German Paragraph Imported $paragraphNumber Sentence $sentenceNumber"
                    },
                    englishSentences = List(10) { sentenceNumber ->
                        "English Paragraph Imported $paragraphNumber Sentence $sentenceNumber"
                    },
                    spanishSentences = List(10) { sentenceNumber ->
                        "Spanish Paragraph Imported $paragraphNumber Sentence $sentenceNumber"
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

fun TestStory.toJson() = """
    {
      "id": "$id",
      "translations": [
        {
          "language": "de",
          "title": "$germanTitle",
          "content": ${content.toJson("de")}
        },
        {
          "language": "en",
          "title": "$englishTitle",
          "content": ${content.toJson("en")}
        },
        {
          "language": "es",
          "title": "$spanishTitle",
          "content": ${content.toJson("es")}
        }
      ]
    }
""".trimIndent()

fun List<TestStoryPart>.toJson(language: String) = joinToString(
    prefix = "[",
    postfix = "]",
    separator = ",\n",
) { it.toJson(language) }

fun TestStoryPart.toJson(language: String) = when (this) {
    is TestStoryPart.Image -> toJson()
    is TestStoryPart.Paragraph -> toJson(language)
}

fun TestStoryPart.Image.toJson() = """
    {
      "type": "image",
      "contentDescription": "$contentDescription",
      "path": "1"
    }
""".trimIndent()

fun TestStoryPart.Paragraph.toJson(language: String) = """
    {
      "type": "paragraph",
      "sentences": ${sentencesAsJson(language)}
    }
""".trimIndent()

fun TestStoryPart.Paragraph.sentencesAsJson(language: String) = when (language) {
    "de" -> germanSentences.toJson()
    "en" -> englishSentences.toJson()
    "es" -> spanishSentences.toJson()
    else -> error("Unknown language code: $language")
}

fun List<String>.toJson() =
    joinToString(prefix = "[", postfix = "]", separator = ", ") { "\"$it\"" }
