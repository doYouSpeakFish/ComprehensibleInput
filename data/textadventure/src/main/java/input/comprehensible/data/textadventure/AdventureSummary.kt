package input.comprehensible.data.textadventure

/**
 * A text adventure as shown in the adventures list.
 */
data class AdventureSummary(
    val id: String,
    val title: String,
    /** The [title] translated into the player's translation language. */
    val translatedTitle: String = "",
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
    /** The fully-resolved URL of the adventure's cover image, or null if it has none. */
    val imageUrl: String? = null,
    /** How far the player has progressed through the adventure. */
    val status: AdventureStatus = AdventureStatus.IN_PROGRESS,
)

/**
 * The progress of an adventure, derived by the backend from its messages: not started until the
 * player sends a message, complete once the latest message ends the story, otherwise in progress.
 */
enum class AdventureStatus(val wireValue: String) {
    NOT_STARTED("not_started"),
    IN_PROGRESS("in_progress"),
    COMPLETE("complete");

    companion object {
        /** Maps a wire value back to a status, defaulting to [IN_PROGRESS] for unknown values. */
        fun fromWire(value: String): AdventureStatus =
            entries.firstOrNull { it.wireValue == value } ?: IN_PROGRESS
    }
}
