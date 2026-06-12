package input.comprehensible.ui.settings.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import input.comprehensible.ui.components.button.LoadingOutlinedButton

/**
 * The "resend code" button shared by the verify-email and password-reset screens.
 *
 * While [cooldownSeconds] is greater than zero the button shows a "Resend in Ns" countdown label
 * (from [countdownTextRes]) and stays disabled; otherwise it shows the normal [resendTextRes] label.
 * Callers pass the already-computed [enabled] flag so the button stays disabled both while a request
 * is in flight and for the duration of the cooldown.
 */
@Composable
internal fun ResendCodeButton(
    cooldownSeconds: Int,
    isResending: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    resendTextRes: Int,
    countdownTextRes: Int,
    buttonTestTag: String,
    loadingIndicatorTestTag: String,
    modifier: Modifier = Modifier,
) {
    LoadingOutlinedButton(
        text = if (cooldownSeconds > 0) {
            stringResource(countdownTextRes, cooldownSeconds)
        } else {
            stringResource(resendTextRes)
        },
        loading = isResending,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.testTag(buttonTestTag),
        loadingIndicatorTestTag = loadingIndicatorTestTag,
    )
}
