package input.comprehensible.ui.settings.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

internal const val SETTINGS_SCREEN_ROUTE = "settings"

internal fun NavController.navigateToSettings(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = SETTINGS_SCREEN_ROUTE,
        builder = builder
    )
}

internal fun NavGraphBuilder.settingsScreen(
    onNavigateUp: () -> Unit,
    onGoToSoftwareLicences: () -> Unit,
) {
    composable(SETTINGS_SCREEN_ROUTE) {
        Settings(
            onNavigateUp = onNavigateUp,
            onGoToSoftwareLicences = onGoToSoftwareLicences
        )
    }
}
