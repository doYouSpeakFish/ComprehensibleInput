package input.comprehensible.data.textadventure.sources.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class TextAdventureWithMessages(
    @Embedded
    val adventure: TextAdventureEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "adventureId",
    )
    val messages: List<TextAdventureMessageEntity>,
)
