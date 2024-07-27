package input.comprehensible.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val languageCode: String,
    @DrawableRes val flag: Int,
    @StringRes val languageName: Int,
    @StringRes val contentDescription: Int,
) {
    SPANISH(
        languageCode = "es",
        flag = R.drawable.es_flag,
        languageName = R.string.language_selector_es_language_name,
        contentDescription = R.string.language_selector_es_content_description,
    ),
    GERMAN(
        languageCode = "de",
        flag = R.drawable.de_flag,
        languageName = R.string.language_selector_de_language_name,
        contentDescription = R.string.language_selector_de_content_description,
    ),
    ENGLISH(
        languageCode = "en",
        flag = R.drawable.gb_flag,
        languageName = R.string.language_selector_en_language_name,
        contentDescription = R.string.language_selector_en_content_description,
    ),
}

/**
 * A language selector that allows the user to select from a drop down list of languages.
 */
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    languageSelection: LanguageSelection,
    onLanguageSelected: (LanguageSelection) -> Unit,
    isMenuShown: Boolean,
    onMenuShownChanged: (Boolean) -> Unit,
    languagesList: List<LanguageSelection> = LanguageSelection.entries,
) {
    Box(modifier) {
        LanguageToggleButton(
            languageSelection = languageSelection,
            onClick = { onMenuShownChanged(!isMenuShown) }
        )
        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            expanded = isMenuShown,
            onDismissRequest = { onMenuShownChanged(false) }
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Learning"
            )
            languagesList.forEach { languageSelection ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(languageSelection.languageName)) },
                    onClick = {
                        onLanguageSelected(languageSelection)
                        onMenuShownChanged(false)
                    },
                    leadingIcon = {
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            painter = painterResource(languageSelection.flag),
                            contentDescription = stringResource(languageSelection.contentDescription),
                            contentScale = ContentScale.FillHeight,
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LanguageToggleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    languageSelection: LanguageSelection,
) {
    OutlinedIconButton(
        modifier = modifier,
        shape = CircleShape,
        onClick = onClick
    ) {
        Image(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            painter = painterResource(languageSelection.flag),
            contentDescription = stringResource(languageSelection.contentDescription),
            contentScale = ContentScale.FillHeight,
        )
    }
}

@DefaultPreview
@Composable
private fun LanguageSelectorPreview() {
    var languageSelection by remember { mutableStateOf(LanguageSelection.ENGLISH) }
    var isMenuShown by remember { mutableStateOf(false) }
    ComprehensibleInputTheme {
        Surface(Modifier.fillMaxSize()) {
            LanguageSelector(
                languageSelection = languageSelection,
                onLanguageSelected = { languageSelection = it },
                isMenuShown = isMenuShown,
                onMenuShownChanged = { isMenuShown = it },
            )
        }
    }
}
