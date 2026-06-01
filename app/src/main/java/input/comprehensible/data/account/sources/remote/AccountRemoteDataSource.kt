package input.comprehensible.data.account.sources.remote

import com.ktin.InjectedSingleton

interface AccountRemoteDataSource {
    suspend fun createAccount(email: String, password: String)
    suspend fun verifyEmail(email: String, code: String)

    companion object : InjectedSingleton<AccountRemoteDataSource>()
}
