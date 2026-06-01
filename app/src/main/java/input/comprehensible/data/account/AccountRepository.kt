package input.comprehensible.data.account

import com.ktin.Singleton
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import timber.log.Timber

class AccountRepository(
    private val remoteDataSource: AccountRemoteDataSource,
    private val localDataSource: AccountLocalDataSource,
) {
    suspend fun createAccount(email: String, password: String): Result<Unit> =
        runCatching { remoteDataSource.createAccount(email, password) }
            .onFailure { Timber.e(it, "Failed to create account") }

    suspend fun verifyEmail(email: String, code: String): Result<Unit> =
        runCatching { remoteDataSource.verifyEmail(email, code) }
            .onFailure { Timber.e(it, "Failed to verify email") }

    suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching {
            val token = remoteDataSource.signIn(email, password)
            localDataSource.saveSession(token, email)
        }.onFailure { Timber.e(it, "Failed to sign in") }

    suspend fun signOut(): Result<Unit> =
        runCatching { localDataSource.clearSession() }
            .onFailure { Timber.e(it, "Failed to sign out") }

    suspend fun getSessionToken(): String? = localDataSource.getSessionToken()

    suspend fun getEmail(): String? = localDataSource.getEmail()

    companion object : Singleton<AccountRepository>() {
        override fun create() = AccountRepository(
            remoteDataSource = AccountRemoteDataSource(),
            localDataSource = AccountLocalDataSource(),
        )
    }
}
