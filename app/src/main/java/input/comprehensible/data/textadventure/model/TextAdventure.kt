package input.comprehensible.data.textadventure.model

data class TextAdventure(
    val id: String,
    val isComplete: Boolean,
    val messages: List<TextAdventureMessage>,
)

data class TextAdventureMessage(
    val id: String,
    val role: TextAdventureRole,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
)

enum class TextAdventureRole {
    USER,
    AI,
}
