package input.comprehensible.data.account

import com.ktin.Singleton
import input.comprehensible.data.account.sources.local.AccountLocalDataSource
import input.comprehensible.data.account.sources.local.Session
import input.comprehensible.data.account.sources.local.UserLocalDataSource
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import input.comprehensible.data.user.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

class AccountRepository(
    private val remoteDataSource: AccountRemoteDataSource,
    private val localDataSource: AccountLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) {
    val session: Flow<Session?> = localDataSource.session
    val user: Flow<UserEntity?> = localDataSource.session.map { current ->
        current?.let { UserEntity(id = it.userId, email = it.email) }
    }

    suspend fun createAccount(email: String, password: String): Result<Unit> =
        runCatching { remoteDataSource.createAccount(email, password) }
            .onFailure { Timber.e(it, "Failed to create account") }

    suspend fun verifyEmail(email: String, code: String): Result<Unit> =
        runCatching { remoteDataSource.verifyEmail(email, code) }
            .onFailure { Timber.e(it, "Failed to verify email") }

    suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching {
            val remoteSession = remoteDataSource.signIn(email, password)
            localDataSource.saveSession(remoteSession.token, email, remoteSession.userId)
            userLocalDataSource.upsertUser(UserEntity(id = remoteSession.userId, email = email))
        }.onFailure { Timber.e(it, "Failed to sign in") }

    suspend fun requestPasswordResetCode(email: String): Result<Unit> =
        runCatching { remoteDataSource.requestPasswordResetCode(email) }
            .onFailure { Timber.e(it, "Failed to request password reset code") }

    suspend fun resetPassword(email: String, password: String, code: String): Result<Unit> =
        runCatching { remoteDataSource.resetPassword(email, password, code) }
            .onFailure { Timber.e(it, "Failed to reset password") }

    suspend fun signOut(): Result<Unit> = runCatching {
        val token = localDataSource.session.first()?.token
        if (token != null) {
            runCatching { remoteDataSource.signOut(token) }
                .onFailure { Timber.e(it, "Failed to revoke server session") }
        }
        localDataSource.clearSession()
    }.onFailure { Timber.e(it, "Failed to sign out") }

    suspend fun deleteAccount(password: String): Result<Unit> = runCatching {
        val current = localDataSource.session.first() ?: throw InvalidCredentialsException()
        remoteDataSource.deleteAccount(current.email, password)
        userLocalDataSource.deleteUser(current.userId)
        localDataSource.clearSession()
    }.onFailure { Timber.e(it, "Failed to delete account") }

    companion object : Singleton<AccountRepository>() {
        override fun create() = AccountRepository(
            remoteDataSource = AccountRemoteDataSource(),
            localDataSource = AccountLocalDataSource(),
            userLocalDataSource = UserLocalDataSource(),
        )
    }
}
