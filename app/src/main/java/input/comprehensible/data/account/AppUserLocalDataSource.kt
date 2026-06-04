package input.comprehensible.data.account

import input.comprehensible.data.AppDb
import input.comprehensible.data.UserEntity
import input.comprehensible.data.account.sources.local.UserLocalDataSource

class AppUserLocalDataSource : UserLocalDataSource {
    override suspend fun insertUser(userId: String) {
        AppDb.getInstance().getUserDao().insertUser(UserEntity(id = userId))
    }
}
