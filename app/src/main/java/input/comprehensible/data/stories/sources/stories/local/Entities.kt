package input.comprehensible.data.stories.sources.stories.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

/**
 * Entity representing a story in the local database.
 */
@Entity
data class StoryEntity(
    @PrimaryKey val id: String,
)

/**
 * Entity representing a title of a story in a given language in the local database.
 */
@Entity(
    primaryKeys = ["storyId", "language"],
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = CASCADE,
        )
    ]
)
data class StoryTitleEntity(
    val storyId: String,
    val language: String,
    val title: String,
    val subtitle: String,
    val featureImagePath: String?,
    val featureImageContentDescription: String?,
)

/**
 * Entity representing an element of a story in the local database.
 */
@Entity(
    primaryKeys = ["storyId", "language", "position"],
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = CASCADE,
        )
    ]
)
data class StoryElementEntity(
    val storyId: String,
    val language: String,
    val position: Int,
    val text: String,
    val imageContentDescription: String?,
    val imagePath: String?,
)
