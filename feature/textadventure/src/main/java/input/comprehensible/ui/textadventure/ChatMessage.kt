package input.comprehensible.ui.textadventure

/**
 * A chat message as rendered by the text adventure chat screen. Each [Paragraph] keeps the
 * learning-language sentences alongside their translations so they can be tapped to translate.
 * This is the UI's own model, kept separate from the data layer's `AdventureMessage`.
 */
data class ChatMessage(
    val id: String,
    val sender: ChatMessageSender,
    val isEnding: Boolean,
    val paragraphs: List<Paragraph>,
) {
    data class Paragraph(
        val sentences: List<String>,
        val translatedSentences: List<String>,
    )
}

enum class ChatMessageSender { AI, USER }
