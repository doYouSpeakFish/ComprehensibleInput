package input.comprehensible.data.sources

import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.user.UserEntity

class FakeUserLocalDataSource : UserLocalDataSource {
    private val storedUsers = mutableMapOf<String, UserEntity>()

    val users: Map<String, UserEntity> get() = storedUsers

    /** When set, [upsertUser] throws this to simulate a local persistence failure. */
    var upsertError: Throwable? = null

    override suspend fun upsertUser(user: UserEntity) {
        upsertError?.let { throw it }
        storedUsers[user.id] = user
    }

    override suspend fun deleteUser(id: String) {
        storedUsers.remove(id)
    }

    override suspend fun getUser(id: String): UserEntity? = storedUsers[id]
}
