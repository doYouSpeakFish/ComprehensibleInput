package input.comprehensible.backend.textadventure

import kotlinx.serialization.Serializable

@Serializable
data class TextAdventureWorldPlan(
    val premise: String,
    val setting: TextAdventureSetting,
    val playerObjective: String,
    val keyNpcs: List<TextAdventureNpc>,
    val inventory: List<TextAdventureInventoryItem>,
    val openThreads: List<String>,
)

@Serializable
data class TextAdventureSetting(
    val locationName: String,
    val mood: String,
    val constraints: List<String>,
)

@Serializable
data class TextAdventureNpc(
    val name: String,
    val role: String,
    val disposition: String,
)

@Serializable
data class TextAdventureInventoryItem(
    val name: String,
    val status: String,
)
