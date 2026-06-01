package input.comprehensible.ui.settings.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.R
import input.comprehensible.ui.components.error.GenericErrorDialog
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
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onSignUpSubmit = viewModel::onSignUpSubmit,
        onCodeChanged = viewModel::onCodeChanged,
        onVerifyEmailSubmit = viewModel::onVerifyEmailSubmit,
        onErrorDismissed = viewModel::onErrorDismissed,
        modifier = modifier,
    )
}

@Composable
private fun AccountScreen(
    uiState: AccountUiState,
    onNavigateUp: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSignUpSubmit: () -> Unit,
    onCodeChanged: (String) -> Unit,
    onVerifyEmailSubmit: () -> Unit,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val step = uiState.step) {
                is AccountUiState.Step.SignUp -> SignUpStep(
                    step = step,
                    onEmailChanged = onEmailChanged,
                    onPasswordChanged = onPasswordChanged,
                    onConfirmPasswordChanged = onConfirmPasswordChanged,
                    onSubmit = onSignUpSubmit,
                )
                is AccountUiState.Step.VerifyEmail -> VerifyEmailStep(
                    step = step,
                    onCodeChanged = onCodeChanged,
                    onSubmit = onVerifyEmailSubmit,
                )
                is AccountUiState.Step.Verified -> VerifiedStep()
            }
        }
    }

    if (uiState.showError) {
        GenericErrorDialog(onDismissRequest = onErrorDismissed)
    }
}

@Composable
private fun SignUpStep(
    step: AccountUiState.Step.SignUp,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
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
            label = { Text(stringResource(R.string.account_sign_up_email_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_up_email_field"),
        )
        OutlinedTextField(
            value = step.password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(R.string.account_sign_up_password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_up_password_field"),
        )
        OutlinedTextField(
            value = step.confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            label = { Text(stringResource(R.string.account_sign_up_confirm_password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_up_confirm_password_field"),
        )
        Button(
            onClick = onSubmit,
            enabled = !step.isLoading && step.isSubmitEnabled(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_sign_up_submit_button"),
        ) {
            if (step.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("account_sign_up_loading_indicator"),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(stringResource(R.string.account_sign_up_submit_button))
            }
        }
    }
}

@Composable
private fun VerifyEmailStep(
    step: AccountUiState.Step.VerifyEmail,
    onCodeChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.account_verify_email_message, step.email),
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedTextField(
            value = step.code,
            onValueChange = onCodeChanged,
            label = { Text(stringResource(R.string.account_verify_email_code_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_verify_email_code_field"),
        )
        Button(
            onClick = onSubmit,
            enabled = !step.isLoading && step.code.length == VERIFICATION_CODE_LENGTH,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_verify_email_submit_button"),
        ) {
            if (step.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("account_verify_email_loading_indicator"),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(stringResource(R.string.account_verify_email_submit_button))
            }
        }
    }
}

@Composable
private fun VerifiedStep(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.account_verified_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("account_verified_message"),
        )
    }
}

@DefaultPreview
@Composable
private fun PreviewAccountSignUp() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState.INITIAL,
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpSubmit = {},
            onCodeChanged = {},
            onVerifyEmailSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
private fun PreviewAccountSignUpLoading() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState(
                step = AccountUiState.Step.SignUp(
                    email = "user@example.com",
                    password = "password12345",
                    confirmPassword = "password12345",
                    isLoading = true,
                ),
            ),
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpSubmit = {},
            onCodeChanged = {},
            onVerifyEmailSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
private fun PreviewAccountVerifyEmail() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState(
                step = AccountUiState.Step.VerifyEmail(email = "user@example.com"),
            ),
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpSubmit = {},
            onCodeChanged = {},
            onVerifyEmailSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
private fun PreviewAccountVerifyEmailLoading() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState(
                step = AccountUiState.Step.VerifyEmail(
                    email = "user@example.com",
                    code = "123456",
                    isLoading = true,
                ),
            ),
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpSubmit = {},
            onCodeChanged = {},
            onVerifyEmailSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
private fun PreviewAccountVerified() {
    ComprehensibleInputTheme {
        AccountScreen(
            uiState = AccountUiState(step = AccountUiState.Step.Verified),
            onNavigateUp = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpSubmit = {},
            onCodeChanged = {},
            onVerifyEmailSubmit = {},
            onErrorDismissed = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
