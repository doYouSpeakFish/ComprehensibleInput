package input.comprehensible.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A signed-in user. It lives in the common module so other features (such as text adventures) can
 * reference it with a foreign key, but creating and removing the row is the account module's
 * responsibility.
 */
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
)
