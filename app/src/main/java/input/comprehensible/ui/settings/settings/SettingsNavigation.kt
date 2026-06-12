package input.comprehensible.ui.settings.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

internal fun NavGraphBuilder.settingsScreen(
    onNavigateUp: () -> Unit,
    accountManagementEnabled: Boolean,
    onGoToAccount: () -> Unit,
    onGoToSoftwareLicences: () -> Unit,
) {
    composable<SettingsRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) {
        Settings(
            onNavigateUp = onNavigateUp,
            accountManagementEnabled = accountManagementEnabled,
            onGoToAccount = onGoToAccount,
            onGoToSoftwareLicences = onGoToSoftwareLicences,
        )
    }
}
