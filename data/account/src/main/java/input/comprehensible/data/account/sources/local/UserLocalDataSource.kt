package input.comprehensible.data.account.sources.local

import com.ktin.InjectedSingleton

interface UserLocalDataSource {
    suspend fun insertUser(userId: String)

    companion object : InjectedSingleton<UserLocalDataSource>()
}
