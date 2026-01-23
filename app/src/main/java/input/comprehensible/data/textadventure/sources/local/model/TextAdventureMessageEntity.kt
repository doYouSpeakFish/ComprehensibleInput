package input.comprehensible.data.textadventure.sources.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import input.comprehensible.data.textadventure.model.TextAdventureRole

@Entity
data class TextAdventureMessageEntity(
    @PrimaryKey
    val id: String,
    val adventureId: String,
    val role: TextAdventureRole,
    val sentences: List<String>,
    val translatedSentences: List<String>,
    val isEnding: Boolean,
    val sequenceIndex: Int,
)
