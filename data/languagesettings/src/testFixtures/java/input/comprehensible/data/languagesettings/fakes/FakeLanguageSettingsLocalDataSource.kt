package input.comprehensible.data.languagesettings.fakes

import input.comprehensible.data.languagesettings.sources.LanguageSettingsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLanguageSettingsLocalDataSource(
    learningLanguageCode: String = "de",
    translationsLanguageCode: String = "en",
    languageLevelCode: String = "B1",
) : LanguageSettingsLocalDataSource {
    private val _learningLanguage = MutableStateFlow(learningLanguageCode)
    private val _translationsLanguage = MutableStateFlow(translationsLanguageCode)
    private val _languageLevel = MutableStateFlow(languageLevelCode)

    override val learningLanguage: Flow<String> = _learningLanguage
    override val translationsLanguage: Flow<String> = _translationsLanguage
    override val languageLevel: Flow<String> = _languageLevel

    override suspend fun setLearningLanguage(language: String) {
        _learningLanguage.value = language
    }

    override suspend fun setTranslationLanguage(language: String) {
        _translationsLanguage.value = language
    }

    override suspend fun setLanguageLevel(level: String) {
        _languageLevel.value = level
    }
}
