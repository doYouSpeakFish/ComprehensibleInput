package input.comprehensible.ui.settings.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.feature.account.R
import input.comprehensible.ui.components.button.LoadingButton
import input.comprehensible.ui.components.error.GenericErrorDialog
import input.comprehensible.ui.components.topbar.SettingsTopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
internal fun ForgotPasswordScreen(
    onNavigateUp: () -> Unit,
    onCodeSent: (email: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.codeSentToEmail) {
        uiState.codeSentToEmail?.let { email ->
            viewModel.onNavigationConsumed()
            onCodeSent(email)
        }
    }

    ForgotPasswordScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        onEmailChanged = viewModel::onEmailChanged,
        onSubmit = viewModel::onSubmit,
        onErrorDismissed = viewModel::onErrorDismissed,
        modifier = modifier,
    )
}

@Composable
private fun ForgotPasswordScreen(
    uiState: ForgotPasswordUiState,
    onNavigateUp: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onErrorDismissed: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                label = { Text(stringResource(R.string.account_forgot_password_email_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_forgot_password_email_field"),
            )
            LoadingButton(
                text = stringResource(R.string.account_forgot_password_submit_button),
                loading = uiState.isLoading,
                onClick = onSubmit,
                enabled = !uiState.isLoading && uiState.isSubmitEnabled(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_forgot_password_submit_button"),
                loadingIndicatorTestTag = "account_forgot_password_loading_indicator",
            )
        }
    }

    if (uiState.showError) {
        GenericErrorDialog(onDismissRequest = onErrorDismissed)
    }
}

@DefaultPreview
@Composable
fun PreviewForgotPassword() {
    ComprehensibleInputTheme {
        ForgotPasswordScreen(
            uiState = ForgotPasswordUiState(),
            onNavigateUp = {},
            onEmailChanged = {},
            onSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewForgotPasswordLoading() {
    ComprehensibleInputTheme {
        ForgotPasswordScreen(
            uiState = ForgotPasswordUiState(
                email = "user@example.com",
                isLoading = true,
            ),
            onNavigateUp = {},
            onEmailChanged = {},
            onSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
