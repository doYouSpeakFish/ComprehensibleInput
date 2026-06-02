package input.comprehensible.data.account.sources.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import input.comprehensible.di.ApplicationProvider
import input.comprehensible.di.IoDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val SESSION_TOKEN = stringPreferencesKey("session_token")
private val EMAIL = stringPreferencesKey("email")

class DefaultAccountLocalDataSource(
    private val context: Context = ApplicationProvider(),
) : AccountLocalDataSource {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "account",
        scope = CoroutineScope(IoDispatcher() + SupervisorJob()),
    )

    override val session: Flow<Session?> = context.dataStore.data.map {
        Session(
            email = it[EMAIL] ?: return@map null,
            token = it[SESSION_TOKEN] ?: return@map null,
        )
    }

    override suspend fun saveSession(token: String, email: String) {
        context.dataStore.edit {
            it[SESSION_TOKEN] = token
            it[EMAIL] = email
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(SESSION_TOKEN)
            it.remove(EMAIL)
        }
    }
}
