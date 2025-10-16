package input.comprehensible.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    @param:DrawableRes val flag: Int,
    @param:StringRes val languageName: Int,
    @param:StringRes val contentDescription: Int,
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
    FRENCH(
        languageCode = "fr",
        flag = R.drawable.fr_flag,
        languageName = R.string.language_selector_fr_language_name,
        contentDescription = R.string.language_selector_fr_content_description,
    ),
    PORTUGUESE(
        languageCode = "pt",
        flag = R.drawable.pt_flag,
        languageName = R.string.language_selector_pt_language_name,
        contentDescription = R.string.language_selector_pt_content_description,
    ),
    INDONESIAN(
        languageCode = "id",
        flag = R.drawable.id_flag,
        languageName = R.string.language_selector_id_language_name,
        contentDescription = R.string.language_selector_id_content_description,
    ),
}

/**
 * A language selector that allows the user to select from two drop down lists of languages, to
 * pick the language they wish to learn and the language they wish to get translations in.
 */
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    onLanguageSelected: (LanguageSelection) -> Unit,
    onTranslationLanguageSelected: (LanguageSelection) -> Unit,
    leaningLanguage: LanguageSelection?,
    translationLanguage: LanguageSelection?,
    languageOptions: List<LanguageSelection>,
) {
    var isLearningLanguageMenuShown by remember { mutableStateOf(false) }
    var isTranslationLanguageMenuShown by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        translationLanguage?.let {
            LanguageSelector(
                title = stringResource(R.string.top_bar_language_picker_translations_label),
                languageSelection = translationLanguage,
                onLanguageSelected = onTranslationLanguageSelected,
                isMenuShown = isTranslationLanguageMenuShown,
                onMenuShownChanged = { isTranslationLanguageMenuShown = it },
                languagesList = languageOptions,
                contentDescription = stringResource(
                    R.string.language_selector_select_translation_language_content_description,
                    stringResource(translationLanguage.languageName)
                )
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null
        )
        leaningLanguage?.let {
            LanguageSelector(
                title = stringResource(R.string.top_bar_language_picker_learning_label),
                languageSelection = leaningLanguage,
                onLanguageSelected = onLanguageSelected,
                isMenuShown = isLearningLanguageMenuShown,
                onMenuShownChanged = { isLearningLanguageMenuShown = it },
                languagesList = languageOptions,
                contentDescription = stringResource(
                    R.string.language_selector_select_learning_language_content_description,
                    stringResource(leaningLanguage.languageName)
                )
            )
        }
    }
}

/**
 * A language selector that allows the user to select from a drop down list of languages.
 */
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    title: String,
    languageSelection: LanguageSelection,
    onLanguageSelected: (LanguageSelection) -> Unit,
    isMenuShown: Boolean,
    onMenuShownChanged: (Boolean) -> Unit,
    languagesList: List<LanguageSelection> = LanguageSelection.entries,
    contentDescription: String,
) {
    Box(modifier) {
        LanguageToggleButton(
            languageSelection = languageSelection,
            onClick = { onMenuShownChanged(!isMenuShown) },
            contentDescription = contentDescription
        )
        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(8.dp)
                ),
            expanded = isMenuShown,
            onDismissRequest = { onMenuShownChanged(false) },
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = title
            )
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
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape,
                            ),
                        painter = painterResource(languageSelection.flag),
                        contentDescription = stringResource(languageSelection.contentDescription),
                        contentScale = ContentScale.FillHeight,
                    )
                }
            )
            (languagesList - languageSelection).forEach { languageSelection ->
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
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = CircleShape,
                                ),
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
    contentDescription: String,
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
            contentDescription = contentDescription,
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
                title = "Learning",
                languageSelection = languageSelection,
                onLanguageSelected = { languageSelection = it },
                isMenuShown = isMenuShown,
                onMenuShownChanged = { isMenuShown = it },
                contentDescription = "",
            )
        }
    }
}
