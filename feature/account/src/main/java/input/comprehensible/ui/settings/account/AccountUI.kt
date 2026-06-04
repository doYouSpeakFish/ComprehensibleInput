package input.comprehensible.ui.settings.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.feature.account.R
import input.comprehensible.ui.components.error.GenericErrorDialog
import input.comprehensible.ui.components.topbar.SettingsTopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
internal fun AccountScreen(
    onNavigateUp: () -> Unit,
    onGoToSignUp: () -> Unit,
    onGoToDeleteAccount: () -> Unit,
    onGoToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AccountScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        onSignInEmailChanged = viewModel::onSignInEmailChanged,
        onSignInPasswordChanged = viewModel::onSignInPasswordChanged,
        onSignInSubmit = viewModel::onSignInSubmit,
        onSignUpButtonClicked = onGoToSignUp,
        onForgotPasswordClicked = onGoToForgotPassword,
        onSignOutClicked = viewModel::onSignOutClicked,
        onDeleteAccountClicked = onGoToDeleteAccount,
        onErrorDismissed = viewModel::onErrorDismissed,
        onInvalidCredentialsErrorDismissed = viewModel::onInvalidCredentialsErrorDismissed,
        modifier = modifier,
    )
}

@Composable
private fun AccountScreen(
    uiState: AccountUiState,
    onNavigateUp: () -> Unit,
    onSignInEmailChanged: (String) -> Unit,
    onSignInPasswordChanged: (String) -> Unit,
    onSignInSubmit: () -> Unit,
    onSignUpButtonClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit,
    onErrorDismissed: () -> Unit,
    onInvalidCredentialsErrorDismissed: () -> Unit,
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
        ) {
            when (val step = uiState.step) {
                is AccountUiState.Step.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
                is AccountUiState.Step.SignedIn -> SignedInStep(
                    step = step,
                    onSignOutClicked = onSignOutClicked,
                    onDeleteAccountClicked = onDeleteAccountClicked,
                )
                is AccountUiState.Step.SignIn -> SignInStep(
                    step = step,
                    onEmailChanged = onSignInEmailChanged,
                    onPasswordChanged = onSignInPasswordChanged,
                    onSignInSubmit = onSignInSubmit,
                    onSignUpClicked = onSignUpButtonClicked,
                    onForgotPasswordClicked = onForgotPasswordClicked,
                )
            }
        }
    }

    if (uiState.showError) {
        GenericErrorDialog(onDismissRequest = onErrorDismissed)
    }

    if (uiState.showInvalidCredentialsError) {
        InvalidCredentialsDialog(onDismissRequest = onInvalidCredentialsErrorDismissed)
    }
}

@Composable
private fun SignedInStep(
    step: AccountUiState.Step.SignedIn,
    onSignOutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = step.email,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("account_signed_in_email"),
        )
        Button(
            onClick = onSignOutClicked,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_out_button"),
        ) {
            Text(stringResource(R.string.account_sign_out_button))
        }
        OutlinedButton(
            onClick = onDeleteAccountClicked,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_delete_account_button"),
        ) {
            Text(stringResource(R.string.account_delete_account_button))
        }
    }
}

@Composable
private fun SignInStep(
    step: AccountUiState.Step.SignIn,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignInSubmit: () -> Unit,
    onSignUpClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = step.email,
            onValueChange = onEmailChanged,
            label = { Text(stringResource(R.string.account_sign_in_email_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_in_email_field"),
        )
        OutlinedTextField(
            value = step.password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(R.string.account_sign_in_password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_in_password_field"),
        )
        Button(
            onClick = onSignInSubmit,
            enabled = !step.isLoading && step.isSignInEnabled(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_in_submit_button"),
        ) {
            if (step.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("account_sign_in_loading_indicator"),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(stringResource(R.string.account_sign_in_submit_button))
            }
        }
        OutlinedButton(
            onClick = onSignUpClicked,
            enabled = !step.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_up_button"),
        ) {
            Text(stringResource(R.string.account_sign_up_button))
        }
        OutlinedButton(
            onClick = onForgotPasswordClicked,
            enabled = !step.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_forgot_password_button"),
        ) {
            Text(stringResource(R.string.account_forgot_password_button))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvalidCredentialsDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .testTag("account_invalid_credentials_dialog"),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.Black),
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .widthIn(min = 280.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.account_invalid_credentials_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.account_invalid_credentials_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Button(onClick = onDismissRequest) {
                    Text(text = stringResource(R.string.account_invalid_credentials_dialog_button))
                }
            }
        }
    }
}

@DefaultPreview
@Composable
fun PreviewAccountSignIn() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState(step = AccountUiState.Step.SignIn()),
            onNavigateUp = {},
            onSignInEmailChanged = {},
            onSignInPasswordChanged = {},
            onSignInSubmit = {},
            onSignUpButtonClicked = {},
            onForgotPasswordClicked = {},
            onSignOutClicked = {},
            onDeleteAccountClicked = {},
            onErrorDismissed = {},
            onInvalidCredentialsErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewAccountSignInLoading() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState(
                step = AccountUiState.Step.SignIn(
                    email = "user@example.com",
                    password = "password12345",
                    isLoading = true,
                ),
            ),
            onNavigateUp = {},
            onSignInEmailChanged = {},
            onSignInPasswordChanged = {},
            onSignInSubmit = {},
            onSignUpButtonClicked = {},
            onForgotPasswordClicked = {},
            onSignOutClicked = {},
            onDeleteAccountClicked = {},
            onErrorDismissed = {},
            onInvalidCredentialsErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewAccountSignedIn() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState(step = AccountUiState.Step.SignedIn(email = "user@example.com")),
            onNavigateUp = {},
            onSignInEmailChanged = {},
            onSignInPasswordChanged = {},
            onSignInSubmit = {},
            onSignUpButtonClicked = {},
            onForgotPasswordClicked = {},
            onSignOutClicked = {},
            onDeleteAccountClicked = {},
            onErrorDismissed = {},
            onInvalidCredentialsErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
