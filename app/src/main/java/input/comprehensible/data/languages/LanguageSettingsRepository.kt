package input.comprehensible.data.languages

import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for storing the user's language settings.
 */
@Singleton
class LanguageSettingsRepository @Inject constructor(
    private val languageSettingsLocalDataSource: LanguageSettingsLocalDataSource,
) {
    /**
     * The language to display translations in.
     */
    val translationsLanguage = languageSettingsLocalDataSource.translationsLanguage

    /**
     * The language the user is learning.
     */
    val learningLanguage = languageSettingsLocalDataSource.learningLanguage

    /**
     * Sets the learning language.
     */
    suspend fun setLearningLanguage(language: String) {
        languageSettingsLocalDataSource.setLearningLanguage(language)
    }

    /**
     * Sets the translation language.
     */
    suspend fun setTranslationLanguage(language: String) {
        languageSettingsLocalDataSource.setTranslationLanguage(language)
    }
}