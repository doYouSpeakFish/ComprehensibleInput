package input.comprehensible.ui.settings.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.R
import input.comprehensible.ui.components.topbar.SettingsTopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
internal fun AccountScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AccountScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
}

@Composable
private fun AccountScreen(
    uiState: AccountUiState,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(
                title = stringResource(R.string.account_screen_title),
                onNavigateUp = onNavigateUp,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@DefaultPreview
@Composable
private fun PreviewAccountScreen() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState.INITIAL,
            onNavigateUp = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
