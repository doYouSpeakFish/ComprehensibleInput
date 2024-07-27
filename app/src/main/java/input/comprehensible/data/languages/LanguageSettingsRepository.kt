package input.comprehensible.data.languages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for storing the user's language settings.
 */
@Singleton
class LanguageSettingsRepository @Inject constructor() {
    private val _translationsLanguage = MutableStateFlow("en")
    private val _learningLanguage = MutableStateFlow("de")

    /**
     * The language to display translations in.
     */
    val translationsLanguage = _translationsLanguage.asStateFlow()

    /**
     * The language the user is learning.
     */
    val learningLanguage = _learningLanguage.asStateFlow()

    /**
     * Sets the learning language.
     */
    fun setLearningLanguage(language: String) {
        _learningLanguage.value = language
    }

    /**
     * Sets the translation language.
     */
    fun setTranslationLanguage(language: String) {
        _translationsLanguage.value = language
    }
}