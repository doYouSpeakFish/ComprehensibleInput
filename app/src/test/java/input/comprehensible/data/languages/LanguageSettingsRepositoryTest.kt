package input.comprehensible.data.languages

import input.comprehensible.data.languages.sources.LanguageSettingsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageSettingsRepositoryTest {

    @Test
    fun learningLanguageChangeKeepsLanguagesDistinct() = runTest {
        val localDataSource = FakeLanguageSettingsLocalDataSource(
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )
        val repository = LanguageSettingsRepository(localDataSource)

        repository.setLearningLanguage("Spanish")

        assertEquals(listOf("English"), localDataSource.translationChanges)
        assertEquals(listOf("Spanish"), localDataSource.learningChanges)
        assertEquals("Spanish", repository.learningLanguage.first())
        assertEquals("English", repository.translationsLanguage.first())
    }

    @Test
    fun translationLanguageChangeKeepsLanguagesDistinct() = runTest {
        val localDataSource = FakeLanguageSettingsLocalDataSource(
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )
        val repository = LanguageSettingsRepository(localDataSource)

        repository.setTranslationLanguage("English")

        assertEquals(listOf("Spanish"), localDataSource.learningChanges)
        assertEquals(listOf("English"), localDataSource.translationChanges)
        assertEquals("Spanish", repository.learningLanguage.first())
        assertEquals("English", repository.translationsLanguage.first())
    }

    @Test
    fun changingToDifferentLanguagesUpdatesWithoutSwapping() = runTest {
        val localDataSource = FakeLanguageSettingsLocalDataSource(
            learningLanguage = "English",
            translationsLanguage = "Spanish",
        )
        val repository = LanguageSettingsRepository(localDataSource)

        repository.setLearningLanguage("French")
        repository.setTranslationLanguage("German")

        assertEquals(listOf("French"), localDataSource.learningChanges)
        assertEquals(listOf("German"), localDataSource.translationChanges)
        assertEquals("French", repository.learningLanguage.first())
        assertEquals("German", repository.translationsLanguage.first())
    }

    private class FakeLanguageSettingsLocalDataSource(
        learningLanguage: String,
        translationsLanguage: String,
    ) : LanguageSettingsLocalDataSource {
        private val learningFlow = MutableStateFlow(learningLanguage)
        private val translationsFlow = MutableStateFlow(translationsLanguage)

        val learningChanges = mutableListOf<String>()
        val translationChanges = mutableListOf<String>()

        override val learningLanguage: Flow<String>
            get() = learningFlow

        override val translationsLanguage: Flow<String>
            get() = translationsFlow

        override suspend fun setLearningLanguage(language: String) {
            learningChanges += language
            learningFlow.value = language
        }

        override suspend fun setTranslationLanguage(language: String) {
            translationChanges += language
            translationsFlow.value = language
        }
    }
}
