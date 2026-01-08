package input.comprehensible.data.sources

import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLanguageSettingsLocalDataSource : LanguageSettingsLocalDataSource {
    private val _learningLanguage = MutableStateFlow("de")
    private val _translationsLanguage = MutableStateFlow("en")

    override val learningLanguage: Flow<String> = _learningLanguage
    override val translationsLanguage: Flow<String> = _translationsLanguage

    override suspend fun setLearningLanguage(language: String) {
        _learningLanguage.value = language
    }

    override suspend fun setTranslationLanguage(language: String) {
        _translationsLanguage.value = language
    }
}
