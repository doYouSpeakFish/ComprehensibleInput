package input.comprehensible.ui.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import input.comprehensible.R
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.ui.components.LanguageSelector
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

/**
 * A top bar with a title and a settings button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    title: String,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(title) },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_button_description)
                )
            }
        }
    )
}

/**
 * A top bar with a title and a settings button, and a language selector for picking the learning
 * language.
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
    onSettingsClick: () -> Unit,
    onTextAdventureClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
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
            IconButton(
                modifier = Modifier.testTag("text_adventure_button"),
                onClick = onTextAdventureClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.text_adventure_button_description)
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_button_description)
                )
            }
        },
    )
}

/**
 * A top bar with a title and a back button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    title: String,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_up)
                )
            }
        },
        title = { Text(title) }
    )
}

@DefaultPreview
@Composable
fun TopBarPreview() {
    ComprehensibleInputTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(onSettingsClick = {}, title = "Title") }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues))
        }
    }
}
