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
)
