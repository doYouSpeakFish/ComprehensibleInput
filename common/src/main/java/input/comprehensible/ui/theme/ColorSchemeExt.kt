package input.comprehensible.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private const val LIGHT_THEME_LUMINANCE_THRESHOLD = 0.5f

private val homeOptionCardLight = Color(0xFFFFFBF2)
private val homeOptionCardDark = Color(0xFF181A18)

/**
 * Whether this is a light colour scheme. Material 3's [ColorScheme] does not expose this directly,
 * so it is derived from the surface luminance, which reflects the active theme. Keeping the check
 * here means UI code never has to branch on the system dark-mode setting itself.
 */
val ColorScheme.isLight: Boolean
    get() = surface.luminance() > LIGHT_THEME_LUMINANCE_THRESHOLD

/**
 * Background colour for the prominent option cards on the home screen: a warm surface that sits
 * just above the background in both light and dark themes.
 */
val ColorScheme.homeOptionCardColor: Color
    get() = if (isLight) homeOptionCardLight else homeOptionCardDark
