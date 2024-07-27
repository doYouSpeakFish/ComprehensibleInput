package input.comprehensible.data.languages.sources

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "language_settings"
)
private val LEARNING_LANGUAGE = stringPreferencesKey("learning_language")
private val TRANSLATION_LANGUAGE = stringPreferencesKey("translation_language")

/**
 * The default implementation of [LanguageSettingsLocalDataSource].
 */
@Singleton
class DefaultLanguageSettingsLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : LanguageSettingsLocalDataSource {

    override val learningLanguage = context.dataStore.data
        .map { it[LEARNING_LANGUAGE] ?: "de" }

    override val translationsLanguage = context.dataStore.data
        .map { it[TRANSLATION_LANGUAGE] ?: "en" }

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
}