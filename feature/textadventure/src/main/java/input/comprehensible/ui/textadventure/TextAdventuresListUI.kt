package input.comprehensible.ui.textadventure

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.feature.textadventure.R
import input.comprehensible.ui.textadventure.TextAdventuresListUiState.AdventureItem

@Composable
internal fun TextAdventuresListScreen(
    onSignInClick: () -> Unit,
    onStartAdventure: () -> Unit,
    onAdventureClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TextAdventuresListViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = TextAdventuresListUiState.INITIAL,
    )
    TextAdventuresListScreen(
        state = state,
        onSignInClick = onSignInClick,
        onStartAdventure = onStartAdventure,
        onAdventureClick = onAdventureClick,
        onDeleteAdventure = viewModel::onDeleteAdventure,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TextAdventuresListScreen(
    state: TextAdventuresListUiState,
    onSignInClick: () -> Unit,
    onStartAdventure: () -> Unit,
    onAdventureClick: (String) -> Unit,
    onDeleteAdventure: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag("text_adventures_screen"),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.text_adventures_title)) })
        },
        floatingActionButton = {
            if (state.isSignedIn) {
                ExtendedFloatingActionButton(
                    onClick = onStartAdventure,
                    modifier = Modifier.testTag("text_adventures_new_button"),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.text_adventures_new)) },
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EarlyAccessNotice()
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (state.isSignedIn) {
                    SignedInContent(
                        state = state,
                        onAdventureClick = onAdventureClick,
                        onDeleteAdventure = onDeleteAdventure,
                    )
                } else {
                    SignInPrompt(onSignInClick = onSignInClick)
                }
            }
        }
    }
}

/**
 * A banner explaining that the text adventures feature is free while in early access. Shown above
 * the list (and the sign-in prompt) so every visitor to the screen sees it.
 */
@Composable
private fun EarlyAccessNotice() {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("text_adventures_early_access"),
    ) {
        Text(
            text = stringResource(R.string.text_adventures_early_access),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Composable
private fun SignedInContent(
    state: TextAdventuresListUiState,
    onAdventureClick: (String) -> Unit,
    onDeleteAdventure: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.showError) {
            Text(
                text = stringResource(R.string.text_adventures_error),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("text_adventures_error"),
            )
        }
        if (state.showBusyMessage) {
            Text(
                text = stringResource(R.string.text_adventure_busy),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("text_adventures_busy"),
            )
        }
        when {
            state.isLoading -> CenteredBox {
                CircularProgressIndicator(modifier = Modifier.testTag("text_adventures_loading"))
            }

            state.adventures.isNotEmpty() -> AdventureList(
                adventures = state.adventures,
                onAdventureClick = onAdventureClick,
                onDeleteAdventure = onDeleteAdventure,
            )

            !state.showError && !state.showBusyMessage -> CenteredBox {
                Text(
                    text = stringResource(R.string.text_adventures_empty),
                    modifier = Modifier.testTag("text_adventures_empty"),
                )
            }
        }
    }
}

@Composable
private fun AdventureList(
    adventures: List<AdventureItem>,
    onAdventureClick: (String) -> Unit,
    onDeleteAdventure: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("text_adventures_list"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = adventures, key = { it.id }) { adventure ->
            AdventureRow(
                adventure = adventure,
                onClick = { onAdventureClick(adventure.id) },
                onDelete = { onDeleteAdventure(adventure.id) },
            )
        }
    }
}

@Composable
private fun AdventureRow(
    adventure: AdventureItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("adventure_${adventure.title}")
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = adventure.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = adventure.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_adventure_${adventure.title}"),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.text_adventures_delete_content_description),
                )
            }
        }
    }
}

@Composable
private fun SignInPrompt(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("text_adventures_sign_in_prompt"),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.text_adventures_sign_in_message),
            style = MaterialTheme.typography.titleMedium,
        )
        Button(
            onClick = onSignInClick,
            modifier = Modifier.testTag("text_adventures_sign_in_button"),
        ) {
            Text(stringResource(R.string.text_adventures_sign_in_button))
        }
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()
    }
}
