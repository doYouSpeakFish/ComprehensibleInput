package input.comprehensible.data.languages

import com.di.singleton.Singleton
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import kotlinx.coroutines.flow.first

/**
 * A repository for storing the user's language settings.
 */
class LanguageSettingsRepository(
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
        val translationLanguage = translationsLanguage.first()
        if (language == translationLanguage) {
            val oldLearningLanguage = languageSettingsLocalDataSource.learningLanguage.first()
            languageSettingsLocalDataSource.setTranslationLanguage(oldLearningLanguage)
        }
        languageSettingsLocalDataSource.setLearningLanguage(language)
    }

    /**
     * Sets the translation language.
     */
    suspend fun setTranslationLanguage(language: String) {
        val learningLanguage = learningLanguage.first()
        if (language == learningLanguage) {
            val oldTranslationLanguage =
                languageSettingsLocalDataSource.translationsLanguage.first()
            languageSettingsLocalDataSource.setLearningLanguage(oldTranslationLanguage)
        }
        languageSettingsLocalDataSource.setTranslationLanguage(language)
    }

    companion object : Singleton<LanguageSettingsRepository>() {
        override fun create() = LanguageSettingsRepository(
            languageSettingsLocalDataSource = LanguageSettingsLocalDataSource()
        )
    }
}
