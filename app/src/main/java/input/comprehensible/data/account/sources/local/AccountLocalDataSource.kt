package input.comprehensible.data.account.sources.local

import com.ktin.InjectedSingleton

interface AccountLocalDataSource {
    suspend fun getSessionToken(): String?
    suspend fun getEmail(): String?
    suspend fun saveSession(token: String, email: String)
    suspend fun clearSession()

    companion object : InjectedSingleton<AccountLocalDataSource>()
}
