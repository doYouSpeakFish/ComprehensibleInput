package input.comprehensible.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import input.comprehensible.feature.home.R
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    textAdventuresEnabled: Boolean,
    onStoriesClick: () -> Unit,
    onTextAdventuresClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.testTag("home_settings_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.home_settings_content_description),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HomeOptionCard(
                label = stringResource(R.string.home_stories),
                onClick = onStoriesClick,
                modifier = Modifier.testTag("home_stories_card"),
            )
            if (textAdventuresEnabled) {
                HomeOptionCard(
                    label = stringResource(R.string.home_text_adventures),
                    onClick = onTextAdventuresClick,
                    modifier = Modifier.testTag("home_text_adventures_card"),
                )
            }
        }
    }
}

@Composable
private fun HomeOptionCard(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = label, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@DefaultPreview
@Composable
fun PreviewHome() {
    ComprehensibleInputTheme {
        HomeScreen(
            textAdventuresEnabled = true,
            onStoriesClick = {},
            onTextAdventuresClick = {},
            onSettingsClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewHomeTextAdventuresDisabled() {
    ComprehensibleInputTheme {
        HomeScreen(
            textAdventuresEnabled = false,
            onStoriesClick = {},
            onTextAdventuresClick = {},
            onSettingsClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
