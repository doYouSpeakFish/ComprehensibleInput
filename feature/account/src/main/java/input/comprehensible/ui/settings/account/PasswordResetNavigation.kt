package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRoute(val email: String)

fun NavGraphBuilder.passwordResetScreen(
    onNavigateUp: () -> Unit,
    onPasswordReset: () -> Unit,
) {
    composable<PasswordResetRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) { entry ->
        PasswordResetScreen(
            email = entry.toRoute<PasswordResetRoute>().email,
            onNavigateUp = onNavigateUp,
            onPasswordReset = onPasswordReset,
        )
    }
}
