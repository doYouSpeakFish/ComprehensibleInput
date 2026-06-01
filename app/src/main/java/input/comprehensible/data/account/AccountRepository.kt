package input.comprehensible.data.account

import com.ktin.Singleton
import input.comprehensible.data.account.sources.remote.AccountRemoteDataSource
import timber.log.Timber

class AccountRepository(
    private val remoteDataSource: AccountRemoteDataSource,
) {
    suspend fun createAccount(email: String, password: String): Result<Unit> =
        runCatching { remoteDataSource.createAccount(email, password) }
            .onFailure { Timber.e(it, "Failed to create account") }

    suspend fun verifyEmail(email: String, code: String): Result<Unit> =
        runCatching { remoteDataSource.verifyEmail(email, code) }
            .onFailure { Timber.e(it, "Failed to verify email") }

    companion object : Singleton<AccountRepository>() {
        override fun create() = AccountRepository(
            remoteDataSource = AccountRemoteDataSource(),
        )
    }
}
