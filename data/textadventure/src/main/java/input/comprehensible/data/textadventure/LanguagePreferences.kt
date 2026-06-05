package input.comprehensible.data.textadventure

import com.ktin.InjectedSingleton
import kotlinx.coroutines.flow.Flow

/**
 * The learning and translation languages a new adventure should be started in. Defined here as an
 * injectable seam so the text adventure feature can read the user's language settings without
 * `:data:textadventure` (or `:feature:textadventure`) depending on the app module that owns them.
 */
interface LanguagePreferences {
    val learningLanguage: Flow<String>
    val translationLanguage: Flow<String>

    companion object : InjectedSingleton<LanguagePreferences>()
}
