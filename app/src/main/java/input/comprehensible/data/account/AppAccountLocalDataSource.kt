package input.comprehensible.data.account

import input.comprehensible.data.AppDb
import input.comprehensible.data.UserEntity
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.DefaultAccountLocalDataSource

class AppAccountLocalDataSource(
    private val base: DefaultAccountLocalDataSource = DefaultAccountLocalDataSource(),
) : AccountLocalDataSource by base {
    override suspend fun saveSession(token: String, email: String, userId: String) {
        AppDb.getInstance().getUserDao().insertUser(UserEntity(id = userId))
        base.saveSession(token = token, email = email, userId = userId)
    }
}
