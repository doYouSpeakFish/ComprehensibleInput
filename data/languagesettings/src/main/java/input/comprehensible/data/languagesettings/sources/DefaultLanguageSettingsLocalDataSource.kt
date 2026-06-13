package input.comprehensible.data.languagesettings.sources

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
import kotlinx.coroutines.flow.map

private val LEARNING_LANGUAGE = stringPreferencesKey("learning_language")
private val TRANSLATION_LANGUAGE = stringPreferencesKey("translation_language")
private val LANGUAGE_LEVEL = stringPreferencesKey("language_level")

// The CEFR level new users start at, matching the backend's backwards-compatible default.
private const val DEFAULT_LANGUAGE_LEVEL = "B1"

/**
 * The default implementation of [LanguageSettingsLocalDataSource].
 */
class DefaultLanguageSettingsLocalDataSource(
    private val context: Context = ApplicationProvider(),
) : LanguageSettingsLocalDataSource {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "language_settings",
        scope = CoroutineScope(IoDispatcher() + SupervisorJob()),
    )

    override val learningLanguage = context.dataStore.data
        .map { it[LEARNING_LANGUAGE] ?: "de" }

    override val translationsLanguage = context.dataStore.data
        .map { it[TRANSLATION_LANGUAGE] ?: "en" }

    override val languageLevel = context.dataStore.data
        .map { it[LANGUAGE_LEVEL] ?: DEFAULT_LANGUAGE_LEVEL }

    override suspend fun setLearningLanguage(language: String) {
        context.dataStore.edit {
            it[LEARNING_LANGUAGE] = language
        }
    }

    override suspend fun setTranslationLanguage(language: String) {
        context.dataStore.edit {
            it[TRANSLATION_LANGUAGE] = language
        }
    }

    override suspend fun setLanguageLevel(level: String) {
        context.dataStore.edit {
            it[LANGUAGE_LEVEL] = level
        }
    }
}
