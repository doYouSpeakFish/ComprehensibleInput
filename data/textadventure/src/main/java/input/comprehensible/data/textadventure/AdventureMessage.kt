package input.comprehensible.data.textadventure

/**
 * A message in an adventure's conversation, as shown in the chat. Each [Paragraph] keeps the
 * learning-language sentences alongside their translations so they can be tapped to translate.
 */
data class AdventureMessage(
    val id: String,
    val sender: AdventureMessageSender,
    val isEnding: Boolean,
    val paragraphs: List<Paragraph>,
) {
    data class Paragraph(
        val sentences: List<String>,
        val translatedSentences: List<String>,
    )
}

enum class AdventureMessageSender { AI, USER }
