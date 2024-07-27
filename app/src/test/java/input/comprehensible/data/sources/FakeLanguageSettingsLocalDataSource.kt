package input.comprehensible.data.sources

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSourceModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeLanguageSettingsLocalDataSource @Inject constructor() : LanguageSettingsLocalDataSource {
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

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LanguageSettingsLocalDataSourceModule::class]
)
interface FakeLanguageSettingsLocalDataSourceModule {
    @Binds
    @Singleton
    fun provideLanguageSettingsLocalDataSource(
        fakeLanguageSettingsLocalDataSource: FakeLanguageSettingsLocalDataSource,
    ): LanguageSettingsLocalDataSource
}
