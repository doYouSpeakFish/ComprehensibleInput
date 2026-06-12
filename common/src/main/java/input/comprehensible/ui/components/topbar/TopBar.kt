package input.comprehensible.ui.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import input.comprehensible.common.R
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.ui.components.LanguageSelector
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

/**
 * A top bar with an up button, a settings button, and a language selector for picking the learning
 * and translation languages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    leaningLanguage: LanguageSelection?,
    translationLanguage: LanguageSelection?,
    languageOptions: List<LanguageSelection>,
    onLanguageSelected: (LanguageSelection) -> Unit,
    onTranslationLanguageSelected: (LanguageSelection) -> Unit,
    onNavigateUp: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_up),
                )
            }
        },
        title = {
            LanguageSelector(
                leaningLanguage = leaningLanguage,
                translationLanguage = translationLanguage,
                languageOptions = languageOptions,
                onLanguageSelected = onLanguageSelected,
                onTranslationLanguageSelected = onTranslationLanguageSelected,
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_button_description),
                )
            }
        },
    )
}

@DefaultPreview
@Composable
fun TopBarPreview() {
    ComprehensibleInputTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(
                    leaningLanguage = LanguageSelection.GERMAN,
                    translationLanguage = LanguageSelection.ENGLISH,
                    languageOptions = LanguageSelection.entries,
                    onLanguageSelected = {},
                    onTranslationLanguageSelected = {},
                    onNavigateUp = {},
                    onSettingsClick = {},
                )
            },
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues))
        }
    }
}
