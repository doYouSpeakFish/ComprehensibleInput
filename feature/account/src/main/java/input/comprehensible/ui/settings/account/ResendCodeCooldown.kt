package input.comprehensible.ui.settings.account

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Number of seconds a resend-code button stays disabled after a code has been requested.
 *
 * This mirrors the backend rate limit on the code-request endpoints: the `email-verification-code`
 * and `password-reset-request` limiters (see the backend's `Application.kt`) both allow one request
 * per `refillPeriod = 30.seconds`. Keeping the client-side cooldown equal to that refill period
 * means the countdown only re-enables the button once the backend will actually accept another
 * request, instead of letting the user trigger a guaranteed rate-limit failure.
 */
internal const val RESEND_CODE_COOLDOWN_SECONDS = 30

/**
 * Launches a once-per-second countdown from [RESEND_CODE_COOLDOWN_SECONDS] down to 0, invoking
 * [onTick] with the number of seconds remaining after each step and a final 0 when the cooldown has
 * elapsed. Callers keep the resend button disabled while the remaining seconds are greater than 0.
 *
 * The returned [Job] lets callers cancel an in-progress countdown before starting a new one.
 */
internal fun CoroutineScope.launchResendCodeCooldown(
    onTick: (secondsRemaining: Int) -> Unit,
): Job = launch {
    for (secondsRemaining in RESEND_CODE_COOLDOWN_SECONDS downTo 1) {
        onTick(secondsRemaining)
        delay(1.seconds)
    }
    onTick(0)
}
