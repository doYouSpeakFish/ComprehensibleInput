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
internal fun SignUpScreen(
    onNavigateUp: () -> Unit,
    onAccountCreated: (email: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.accountCreatedForEmail) {
        uiState.accountCreatedForEmail?.let { email ->
            viewModel.onNavigationConsumed()
            onAccountCreated(email)
        }
    }

    SignUpScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onSubmit = viewModel::onSubmit,
        onErrorDismissed = viewModel::onErrorDismissed,
        modifier = modifier,
    )
}

@Composable
private fun SignUpScreen(
    uiState: SignUpUiState,
    onNavigateUp: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    passwordsInitiallyVisible: Boolean = false,
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
                label = { Text(stringResource(R.string.account_sign_up_email_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_sign_up_email_field"),
            )
            PasswordTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = stringResource(R.string.account_sign_up_password_label),
                fieldTestTag = "account_sign_up_password_field",
                toggleTestTag = "account_sign_up_password_toggle",
                modifier = Modifier.fillMaxWidth(),
                initiallyVisible = passwordsInitiallyVisible,
            )
            PasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                label = stringResource(R.string.account_sign_up_confirm_password_label),
                fieldTestTag = "account_sign_up_confirm_password_field",
                toggleTestTag = "account_sign_up_confirm_password_toggle",
                modifier = Modifier.fillMaxWidth(),
                initiallyVisible = passwordsInitiallyVisible,
            )
            LoadingButton(
                text = stringResource(R.string.account_sign_up_submit_button),
                loading = uiState.isLoading,
                onClick = onSubmit,
                enabled = !uiState.isLoading && uiState.isSubmitEnabled(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_sign_up_submit_button"),
                loadingIndicatorTestTag = "account_sign_up_loading_indicator",
            )
        }
    }

    if (uiState.showError) {
        GenericErrorDialog(onDismissRequest = onErrorDismissed)
    }
}

@DefaultPreview
@Composable
fun PreviewSignUp() {
    ComprehensibleInputTheme {
        SignUpScreen(
            uiState = SignUpUiState(),
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewSignUpLoading() {
    ComprehensibleInputTheme {
        SignUpScreen(
            uiState = SignUpUiState(
                email = "user@example.com",
                password = "password12345",
                confirmPassword = "password12345",
                isLoading = true,
            ),
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewSignUpPasswordsVisible() {
    ComprehensibleInputTheme {
        SignUpScreen(
            uiState = SignUpUiState(
                email = "user@example.com",
                password = "password12345",
                confirmPassword = "password12345",
            ),
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSubmit = {},
            onErrorDismissed = {},
            passwordsInitiallyVisible = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
