package input.comprehensible.data.textadventure.fakes

import input.comprehensible.data.textadventure.LanguagePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake [LanguagePreferences] returning fixed languages for tests.
 */
class FakeLanguagePreferences(
    learningLanguageCode: String = "en",
    translationLanguageCode: String = "es",
) : LanguagePreferences {
    override val learningLanguage: Flow<String> = flowOf(learningLanguageCode)
    override val translationLanguage: Flow<String> = flowOf(translationLanguageCode)
}
