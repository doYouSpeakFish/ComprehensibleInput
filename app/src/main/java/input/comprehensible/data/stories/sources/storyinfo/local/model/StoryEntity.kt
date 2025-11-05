package input.comprehensible.data.stories.sources.storyinfo.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoryEntity(
    @PrimaryKey val id: String,
    val positionPartId: String = "",
    val positionElementIndex: Int = 0,
)
