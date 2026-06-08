package input.comprehensible.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val homeOptionCardLight = Color(0xFFFFFBF2)
private val homeOptionCardDark = Color(0xFF181A18)

/**
 * Background colour for the prominent option cards on the home screen: a warm surface that sits
 * just above the background in both light and dark themes. The variant follows the theme that
 * [ComprehensibleInputTheme] is rendering (via [LocalIsDarkTheme]), so callers read it straight off
 * the colour scheme and never branch on the dark-mode setting themselves.
 */
@Composable
@ReadOnlyComposable
fun ColorScheme.homeOptionCardColor(): Color =
    if (LocalIsDarkTheme.current) homeOptionCardDark else homeOptionCardLight
