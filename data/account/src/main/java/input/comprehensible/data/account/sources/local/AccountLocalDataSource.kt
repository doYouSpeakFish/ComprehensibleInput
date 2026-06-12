package input.comprehensible.data.account.sources.local

import com.ktin.InjectedSingleton
import kotlinx.coroutines.flow.Flow

data class Session(
    val email: String,
    val token: String,
    val userId: String,
)

interface AccountLocalDataSource {
    val session: Flow<Session?>
    suspend fun saveSession(token: String, email: String, userId: String)
    suspend fun clearSession()

    companion object : InjectedSingleton<AccountLocalDataSource>()
}
