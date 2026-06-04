package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRoute(val email: String)

fun NavGraphBuilder.passwordResetScreen(
    onNavigateUp: () -> Unit,
    onPasswordReset: () -> Unit,
) {
    composable<PasswordResetRoute> {
        PasswordResetScreen(
            onNavigateUp = onNavigateUp,
            onPasswordReset = onPasswordReset,
        )
    }
}
