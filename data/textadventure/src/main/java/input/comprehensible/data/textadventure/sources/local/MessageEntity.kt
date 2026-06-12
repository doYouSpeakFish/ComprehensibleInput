package input.comprehensible.data.textadventure.sources.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single message in an adventure's conversation. Foreign-keyed to its [AdventureEntity] so the
 * messages (and their sentences) are removed when the adventure is deleted. [position] gives the
 * display order within the adventure.
 */
@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = AdventureEntity::class,
            parentColumns = ["id"],
            childColumns = ["adventureId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("adventureId")],
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val adventureId: String,
    val parentId: String?,
    val sender: String,
    val isEnding: Boolean,
    val position: Int,
)
