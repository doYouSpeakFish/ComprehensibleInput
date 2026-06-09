package input.comprehensible.data.textadventure

/**
 * A text adventure as shown in the adventures list.
 */
data class AdventureSummary(
    val id: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
    /** The fully-resolved URL of the adventure's cover image, or null if it has none. */
    val imageUrl: String? = null,
)
