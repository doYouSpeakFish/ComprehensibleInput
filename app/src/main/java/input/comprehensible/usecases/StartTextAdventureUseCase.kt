package input.comprehensible.usecases

import input.comprehensible.data.account.sources.local.Session
import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.textadventures.TextAdventuresRepository
import kotlinx.coroutines.flow.first

class StartTextAdventureUseCase(
    private val repository: TextAdventuresRepository = TextAdventuresRepository(),
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
) {
    suspend operator fun invoke(session: Session): String {
        val learningLanguage = languageSettingsRepository.learningLanguage.first()
        val translationsLanguage = languageSettingsRepository.translationsLanguage.first()
        return repository.startNewAdventure(
            token = session.token,
            userId = session.userId,
            learningLanguage = learningLanguage,
            translationsLanguage = translationsLanguage,
        )
    }
}
