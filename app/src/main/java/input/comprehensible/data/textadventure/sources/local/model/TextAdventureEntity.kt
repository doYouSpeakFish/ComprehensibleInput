package input.comprehensible.data.textadventure.sources.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TextAdventureEntity(
    @PrimaryKey
    val id: String,
    val isComplete: Boolean,
)
