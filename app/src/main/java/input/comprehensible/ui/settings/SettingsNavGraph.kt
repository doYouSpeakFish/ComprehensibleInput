package input.comprehensible.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import input.comprehensible.ui.settings.account.AccountRoute
import input.comprehensible.ui.settings.account.accountScreen
import input.comprehensible.ui.settings.settings.SettingsRoute
import input.comprehensible.ui.settings.settings.settingsScreen
import input.comprehensible.ui.settings.softwarelicences.SoftwareLicencesRoute
import input.comprehensible.ui.settings.softwarelicences.softwareLicences
import input.comprehensible.util.FeatureFlags
import kotlinx.serialization.Serializable

@Serializable
data object SettingsGraphRoute

internal fun NavGraphBuilder.settingsNavGraph(navController: NavController) {
    val featureFlags = FeatureFlags()
    navigation<SettingsGraphRoute>(
        startDestination = SettingsRoute,
    ) {
        settingsScreen(
            onNavigateUp = navController::navigateUp,
            accountManagementEnabled = featureFlags.accountManagementEnabled,
            onGoToAccount = { navController.navigate(AccountRoute) },
            onGoToSoftwareLicences = { navController.navigate(SoftwareLicencesRoute) },
        )
        accountScreen(
            onNavigateUp = navController::navigateUp,
        )
        softwareLicences(
            onNavigateUp = navController::navigateUp,
        )
    }
}
