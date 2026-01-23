package input.comprehensible.data.textadventures.model

/**
 * Represents a text adventure and its messages.
 */
data class TextAdventure(
    val id: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val messages: List<TextAdventureMessage>,
    val isComplete: Boolean,
)

/**
 * A single message in a text adventure.
 */
data class TextAdventureMessage(
    val id: String,
    val sender: TextAdventureMessageSender,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
)

/**
 * The sender of a text adventure message.
 */
enum class TextAdventureMessageSender {
    AI,
    USER,
}

/**
 * A list item for a text adventure.
 */
data class TextAdventureSummary(
    val id: String,
    val title: String,
    val isComplete: Boolean,
    val updatedAt: Long,
)
