package input.comprehensible.data.stories.sources.storyinfo.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity
data class StoryEntity(
    @PrimaryKey val id: String,
    val partId: String? = null,
    @ColumnInfo(defaultValue = "NULL")
    val lastChosenPartId: String? = null,
    val position: Int = 0,
)
