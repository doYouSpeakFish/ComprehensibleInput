package input.comprehensible.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import input.comprehensible.R
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

/**
 * A selection of languages that can be used in the app.
 */
enum class LanguageSelection(
    @DrawableRes val flag: Int,
    @StringRes val contentDescription: Int,
) {
    GERMAN(
        R.drawable.de_flag,
        R.string.language_selector_de_content_description,
    ),
    ENGLISH(
        R.drawable.gb_flag,
        R.string.language_selector_en_content_description,
    ),
}

/**
 * A language selector that allows the user to switch between the learning language and the
 * translation language.
 */
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    learningLanguage: LanguageSelection,
    translationLanguage: LanguageSelection,
    onTranslationsEnabledChanged: (Boolean) -> Unit,
    areTranslationsEnabled: Boolean,
) {
    Row(modifier) {
        LanguageToggleButton(
            flag = painterResource(translationLanguage.flag),
            contentDescription = stringResource(translationLanguage.contentDescription),
            onSelectedChange = { onTranslationsEnabledChanged(true) },
            isSelected = areTranslationsEnabled,
        )
        Spacer(modifier = Modifier.size(8.dp))
        LanguageToggleButton(
            flag = painterResource(learningLanguage.flag),
            contentDescription = stringResource(learningLanguage.contentDescription),
            onSelectedChange = { onTranslationsEnabledChanged(false) },
            isSelected = !areTranslationsEnabled,
        )
    }
}

@Composable
private fun LanguageToggleButton(
    modifier: Modifier = Modifier,
    flag: Painter,
    contentDescription: String,
    onSelectedChange: (Boolean) -> Unit,
    isSelected: Boolean
) {
    OutlinedIconToggleButton(
        modifier = modifier,
        checked = isSelected,
        onCheckedChange = onSelectedChange,
        shape = CircleShape,
        colors = IconButtonDefaults.outlinedIconToggleButtonColors(
            checkedContainerColor = Color.Transparent,
        ),
        border = IconButtonDefaults.outlinedIconToggleButtonBorder(
            enabled = true,
            checked = !isSelected,
        ),
    ) {
        Image(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            painter = flag,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillHeight,
        )
    }
}

@DefaultPreview
@Composable
private fun LanguageSelectorPreview() {
    var areTranslationsEnabled by remember { mutableStateOf(true) }
    ComprehensibleInputTheme {
        LanguageSelector(
            learningLanguage = LanguageSelection.GERMAN,
            translationLanguage = LanguageSelection.ENGLISH,
            onTranslationsEnabledChanged = { areTranslationsEnabled = !it },
            areTranslationsEnabled = areTranslationsEnabled,
        )
    }
}
