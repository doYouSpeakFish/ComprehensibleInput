package input.comprehensible.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import input.comprehensible.ui.settings.settings.SETTINGS_SCREEN_ROUTE
import input.comprehensible.ui.settings.settings.settingsScreen
import input.comprehensible.ui.settings.softwarelicences.navigateToSoftwareLicences
import input.comprehensible.ui.settings.softwarelicences.softwareLicences

internal const val SETTINGS_ROUTE = "settings_graph"

internal fun NavGraphBuilder.settingsNavGraph(navController: NavController) {
    navigation(
        route = SETTINGS_ROUTE,
        startDestination = SETTINGS_SCREEN_ROUTE,
    ) {
        settingsScreen(
            onNavigateUp = navController::navigateUp,
            onGoToSoftwareLicences = navController::navigateToSoftwareLicences,
        )
        softwareLicences(
            onNavigateUp = navController::navigateUp,
        )
    }
}
