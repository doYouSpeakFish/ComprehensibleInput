package input.comprehensible.data.account.sources.remote

import com.ktin.InjectedSingleton

interface AccountRemoteDataSource {
    suspend fun createAccount(email: String, password: String)
    suspend fun verifyEmail(email: String, code: String)
    suspend fun signIn(email: String, password: String): String
    suspend fun signOut(token: String)
    suspend fun requestPasswordResetCode(email: String)
    suspend fun resetPassword(email: String, password: String, code: String)

    companion object : InjectedSingleton<AccountRemoteDataSource>()
}
