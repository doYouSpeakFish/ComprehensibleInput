package input.comprehensible.data.sources

import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.user.UserEntity

class FakeUserLocalDataSource : UserLocalDataSource {
    private val storedUsers = mutableMapOf<String, UserEntity>()

    val users: Map<String, UserEntity> get() = storedUsers

    override suspend fun upsertUser(user: UserEntity) {
        storedUsers[user.id] = user
    }

    override suspend fun deleteUser(id: String) {
        storedUsers.remove(id)
    }

    override suspend fun getUser(id: String): UserEntity? = storedUsers[id]
}
