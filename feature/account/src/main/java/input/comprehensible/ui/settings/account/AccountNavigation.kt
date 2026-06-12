package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object AccountRoute

fun NavGraphBuilder.accountScreen(
    onNavigateUp: () -> Unit,
    onGoToSignUp: () -> Unit,
    onGoToDeleteAccount: () -> Unit,
    onGoToForgotPassword: () -> Unit,
) {
    composable<AccountRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) {
        AccountScreen(
            onNavigateUp = onNavigateUp,
            onGoToSignUp = onGoToSignUp,
            onGoToDeleteAccount = onGoToDeleteAccount,
            onGoToForgotPassword = onGoToForgotPassword,
        )
    }
}
