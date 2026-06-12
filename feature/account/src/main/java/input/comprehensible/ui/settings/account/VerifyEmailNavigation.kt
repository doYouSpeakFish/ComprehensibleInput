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
data class VerifyEmailRoute(val email: String)

fun NavGraphBuilder.verifyEmailScreen(
    onNavigateUp: () -> Unit,
    onVerified: () -> Unit,
) {
    composable<VerifyEmailRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) { entry ->
        VerifyEmailScreen(
            email = entry.toRoute<VerifyEmailRoute>().email,
            onNavigateUp = onNavigateUp,
            onVerified = onVerified,
        )
    }
}
