package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object DeleteAccountRoute

fun NavGraphBuilder.deleteAccountScreen(
    onNavigateUp: () -> Unit,
    onAccountDeleted: () -> Unit,
) {
    composable<DeleteAccountRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) {
        DeleteAccountScreen(
            onNavigateUp = onNavigateUp,
            onAccountDeleted = onAccountDeleted,
        )
    }
}
