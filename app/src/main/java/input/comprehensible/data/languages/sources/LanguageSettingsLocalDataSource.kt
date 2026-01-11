package input.comprehensible.data.languages.sources

import input.comprehensible.util.InjectedSingleton
import kotlinx.coroutines.flow.Flow

/**
 * A data source for storing the user's language settings.
 */
interface LanguageSettingsLocalDataSource {
    /**
     * The language the user is learning.
     */
    val learningLanguage: Flow<String>

    /**
     * The language to display translations in.
     */
    val translationsLanguage: Flow<String>

    /**
     * Sets the learning language.
     */
    suspend fun setLearningLanguage(language: String)

    /**
     * Sets the translation language.
     */
    suspend fun setTranslationLanguage(language: String)

    companion object : InjectedSingleton<LanguageSettingsLocalDataSource>()
}
