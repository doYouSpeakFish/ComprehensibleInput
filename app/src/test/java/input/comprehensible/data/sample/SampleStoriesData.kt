package input.comprehensible.data.sample

object SampleStoriesData {
    val listOf100Stories = List(100) { storyNumber ->
        TestStory(
            id = "$storyNumber",
            germanTitle = "German Title $storyNumber",
            englishTitle = "English Title $storyNumber",
            spanishTitle = "Spanish Title $storyNumber",
            parts = listOf(
                TestStoryPartSegment(
                    id = "main",
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
            )
        )
    }.reversed()

    val chooseYourOwnAdventureStory = TestStory(
        id = "choose-path",
        germanTitle = "Der verschwundene Schlüssel",
        englishTitle = "The Missing Key",
        spanishTitle = "La llave perdida",
        parts = listOf(
            TestStoryPartSegment(
                id = "start",
                content = listOf(
                    TestStoryPart.Paragraph(
                        germanSentences = listOf(
                            "Lina findet vor der Bibliothek einen alten Schlüssel.",
                            "Sie fragt sich, ob er einer Tür in der Stadt gehört.",
                            "Sie möchte wissen, was sie damit tun soll."
                        ),
                        englishSentences = listOf(
                            "Lina finds an old key outside the library.",
                            "She wonders if it belongs to a door in the town.",
                            "She wants to know what to do with it."
                        ),
                        spanishSentences = listOf(
                            "Lina encuentra una llave vieja frente a la biblioteca.",
                            "Se pregunta si pertenece a una puerta de la ciudad.",
                            "Quiere saber qué hacer con ella."
                        )
                    )
                ),
                choices = listOf(
                    TestStoryChoice(
                        targetPartId = "keep_key",
                        textByLanguage = mapOf(
                            "de" to "Sie behält den Schlüssel.",
                            "en" to "She keeps the key.",
                            "es" to "Ella se queda con la llave."
                        )
                    ),
                    TestStoryChoice(
                        targetPartId = "return_key",
                        textByLanguage = mapOf(
                            "de" to "Sie bringt den Schlüssel zum Fundbüro.",
                            "en" to "She takes the key to the lost property office.",
                            "es" to "Lleva la llave a objetos perdidos."
                        )
                    )
                )
            ),
            TestStoryPartSegment(
                id = "keep_key",
                content = listOf(
                    TestStoryPart.Paragraph(
                        germanSentences = listOf(
                            "Lina steckt den Schlüssel in ihre Tasche und geht weiter.",
                            "Am Abend entdeckt sie eine kleine Tür hinter dem Rathaus.",
                            "Der Schlüssel passt, und sie findet eine ruhige Leseecke."
                        ),
                        englishSentences = listOf(
                            "Lina slips the key into her pocket and walks on.",
                            "In the evening she discovers a small door behind the town hall.",
                            "The key fits, and she finds a quiet reading nook."
                        ),
                        spanishSentences = listOf(
                            "Lina guarda la llave en el bolsillo y sigue caminando.",
                            "Por la tarde descubre una puerta pequeña detrás del ayuntamiento.",
                            "La llave encaja y encuentra un rincón tranquilo para leer."
                        )
                    )
                )
            ),
            TestStoryPartSegment(
                id = "return_key",
                content = listOf(
                    TestStoryPart.Paragraph(
                        germanSentences = listOf(
                            "Lina gibt den Schlüssel im Fundbüro ab.",
                            "Die Mitarbeiterin bedankt sich herzlich und bietet ihr einen Tee an.",
                            "Lina fühlt sich stolz, weil sie etwas Gutes getan hat."
                        ),
                        englishSentences = listOf(
                            "Lina hands the key to the lost property office.",
                            "The clerk thanks her warmly and offers her a tea.",
                            "Lina feels proud for doing something kind."
                        ),
                        spanishSentences = listOf(
                            "Lina entrega la llave en objetos perdidos.",
                            "La encargada le agradece con cariño y le ofrece un té.",
                            "Lina se siente orgullosa por hacer algo amable."
                        )
                    )
                )
            )
        )
    )
}

data class TestStory(
    val id: String,
    val germanTitle: String,
    val englishTitle: String,
    val spanishTitle: String,
    val parts: List<TestStoryPartSegment>,
) {
    val paragraphs: List<TestStoryPart.Paragraph> =
        parts.flatMap { part -> part.content.filterIsInstance<TestStoryPart.Paragraph>() }

    val images: List<TestStoryPart.Image> =
        parts.flatMap { part -> part.content.filterIsInstance<TestStoryPart.Image>() }

    fun getPart(id: String) = parts.first { it.id == id }
}

sealed interface TestStoryPart {
    data class Paragraph(
        val germanSentences: List<String>,
        val englishSentences: List<String>,
        val spanishSentences: List<String>,
    ) : TestStoryPart

    data class Image(val contentDescription: String) : TestStoryPart
}

data class TestStoryPartSegment(
    val id: String,
    val content: List<TestStoryPart>,
    val choices: List<TestStoryChoice> = emptyList(),
)

data class TestStoryChoice(
    val targetPartId: String,
    val textByLanguage: Map<String, String>,
)

fun List<TestStoryPart>.filterParagraphs() = filterIsInstance<TestStoryPart.Paragraph>()