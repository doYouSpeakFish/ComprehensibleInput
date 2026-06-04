package input.comprehensible.data.account

import com.ktin.Singleton
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.Session
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber

class AccountRepository(
    private val remoteDataSource: AccountRemoteDataSource,
    private val localDataSource: AccountLocalDataSource,
) {
    val session: Flow<Session?> = localDataSource.session

    suspend fun createAccount(email: String, password: String): Result<Unit> =
        runCatching { remoteDataSource.createAccount(email, password) }
            .onFailure { Timber.e(it, "Failed to create account") }

    suspend fun verifyEmail(email: String, code: String): Result<Unit> =
        runCatching { remoteDataSource.verifyEmail(email, code) }
            .onFailure { Timber.e(it, "Failed to verify email") }

    suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching {
            val signInData = remoteDataSource.signIn(email, password)
            localDataSource.saveSession(
                token = signInData.token,
                email = email,
                userId = signInData.userId,
            )
        }.onFailure { Timber.e(it, "Failed to sign in") }

    suspend fun requestPasswordResetCode(email: String): Result<Unit> =
        runCatching { remoteDataSource.requestPasswordResetCode(email) }
            .onFailure { Timber.e(it, "Failed to request password reset code") }

    suspend fun resetPassword(email: String, password: String, code: String): Result<Unit> =
        runCatching { remoteDataSource.resetPassword(email, password, code) }
            .onFailure { Timber.e(it, "Failed to reset password") }

    suspend fun signOut(): Result<Unit> = runCatching {
        val token = localDataSource.session.first()?.token
        localDataSource.clearSession()
        if (token != null) {
            runCatching { remoteDataSource.signOut(token) }
                .onFailure { Timber.e(it, "Failed to revoke server session") }
        }
    }.onFailure { Timber.e(it, "Failed to sign out") }

    companion object : Singleton<AccountRepository>() {
        override fun create() = AccountRepository(
            remoteDataSource = AccountRemoteDataSource(),
            localDataSource = AccountLocalDataSource(),
        )
    }
}
