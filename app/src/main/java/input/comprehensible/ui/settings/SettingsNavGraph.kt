package input.comprehensible.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import input.comprehensible.ui.settings.settings.SettingsRoute
import input.comprehensible.ui.settings.settings.settingsScreen
import input.comprehensible.ui.settings.softwarelicences.SoftwareLicencesRoute
import input.comprehensible.ui.settings.softwarelicences.softwareLicences
import kotlinx.serialization.Serializable

@Serializable
data object SettingsGraphRoute

internal fun NavGraphBuilder.settingsNavGraph(navController: NavController) {
    navigation<SettingsGraphRoute>(
        startDestination = SettingsRoute,
    ) {
        settingsScreen(
            onNavigateUp = navController::navigateUp,
            onGoToSoftwareLicences = { navController.navigate(SoftwareLicencesRoute) },
        )
        softwareLicences(
            onNavigateUp = navController::navigateUp,
        )
    }
}
