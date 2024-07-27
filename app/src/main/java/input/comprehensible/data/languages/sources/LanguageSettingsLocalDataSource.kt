package input.comprehensible.data.languages.sources

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

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
}

/**
 * Hilt module for injecting the default implementation of [LanguageSettingsLocalDataSource].
 */
@Module
@InstallIn(SingletonComponent::class)
class LanguageSettingsLocalDataSourceModule {
    @Provides
    @Singleton
    fun provideLanguageSettingsLocalDataSource(
        @ApplicationContext context: Context
    ): LanguageSettingsLocalDataSource = DefaultLanguageSettingsLocalDataSource(
        context = context,
    )
}
