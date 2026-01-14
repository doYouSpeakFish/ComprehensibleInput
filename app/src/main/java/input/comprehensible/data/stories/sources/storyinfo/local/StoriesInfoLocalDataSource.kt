package input.comprehensible.data.stories.sources.storyinfo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.di.singleton.InjectedSingleton
import input.comprehensible.data.stories.sources.storyinfo.local.model.StoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

@Dao
interface StoriesInfoLocalDataSource {
    @Insert(onConflict = IGNORE)
    suspend fun insertStory(story: StoryEntity)

    @Query(
        """
            SELECT * FROM StoryEntity
            WHERE id = :id
            LIMIT 1
        """
    )
    fun getStory(id: String): Flow<StoryEntity?>

    @Query(
        """
            UPDATE StoryEntity
            SET position = :position 
            WHERE id = :id
        """
    )
    suspend fun updateStory(id: String, position: Int)

    @Query(
        """
            UPDATE StoryEntity
            SET partId = :partId,
                position = 0
            WHERE id = :id
        """
    )
    suspend fun updateStory(id: String, partId: String)

    @Query(
        """
            UPDATE StoryEntity
            SET lastChosenPartId = :lastChosenPartId
            WHERE id = :id
        """
    )
    suspend fun updateStoryChoice(
        id: String,
        lastChosenPartId: String,
    )

    companion object : InjectedSingleton<StoriesInfoLocalDataSource>()
}

fun StoriesInfoLocalDataSource.getOrCreateStory(
    id: String
): Flow<StoryEntity> = getStory(id)
    .onEach { storyEntity ->
        if (storyEntity == null) {
            insertStory(StoryEntity(id = id))
        }
    }
    .filterNotNull()
    .distinctUntilChanged()
