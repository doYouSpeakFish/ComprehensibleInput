package input.comprehensible.ui.textadventure

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

/**
 * Resolves a language code (e.g. "de") into its name in the current UI locale, so adventures show a
 * proper language ("German", or "Deutsch" when the app itself is shown in German) rather than the
 * raw code. Falls back to the supplied value when it is not a recognisable language tag, so any
 * unexpected input still renders something sensible.
 */
@Composable
@ReadOnlyComposable
internal fun languageDisplayName(language: String): String {
    val locale = LocalConfiguration.current.locales[0]
    val displayName = Locale.forLanguageTag(language).getDisplayLanguage(locale)
    return displayName.ifBlank { language }
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}
