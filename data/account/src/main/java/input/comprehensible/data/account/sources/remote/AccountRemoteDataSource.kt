package input.comprehensible.data.account.sources.remote

import com.ktin.InjectedSingleton

data class SignInData(val token: String, val userId: String)

interface AccountRemoteDataSource {
    suspend fun createAccount(email: String, password: String)
    suspend fun verifyEmail(email: String, code: String)
    suspend fun signIn(email: String, password: String): SignInData
    suspend fun signOut(token: String)

    companion object : InjectedSingleton<AccountRemoteDataSource>()
}
