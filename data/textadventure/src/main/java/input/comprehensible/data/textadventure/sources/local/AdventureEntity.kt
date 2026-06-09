package input.comprehensible.data.textadventure.sources.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import input.comprehensible.data.user.UserEntity

/**
 * A locally cached text adventure. It is scoped to the signed-in [UserEntity] through a foreign key
 * so a user only ever sees their own adventures and the rows are removed automatically when the
 * account (and therefore the user row) is deleted.
 */
@Entity(
    tableName = "adventure",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId")],
)
data class AdventureEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val learningLanguage: String,
    val translationLanguage: String,
    val updatedAt: Long,
    /** The fully-resolved URL of the adventure's cover image, or null if it has none. */
    val imageUrl: String? = null,
)
