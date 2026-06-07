package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRoute(val email: String)

fun NavGraphBuilder.passwordResetScreen(
    onNavigateUp: () -> Unit,
    onPasswordReset: () -> Unit,
) {
    composable<PasswordResetRoute> { entry ->
        PasswordResetScreen(
            email = entry.toRoute<PasswordResetRoute>().email,
            onNavigateUp = onNavigateUp,
            onPasswordReset = onPasswordReset,
        )
    }
}
