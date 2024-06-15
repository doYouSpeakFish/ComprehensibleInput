package input.comprehensible.ui.settings.softwarelicences

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

internal const val SOFTWARE_LICENCES_ROUTE = "softwareLicences"

internal fun NavController.navigateToSoftwareLicences(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(
        route = SOFTWARE_LICENCES_ROUTE,
        builder = builder
    )
}

internal fun NavGraphBuilder.softwareLicences(
    onNavigateUp: () -> Unit
) {
    composable(SOFTWARE_LICENCES_ROUTE) {
        SoftwareLicences(
            onNavigateUp = onNavigateUp,
        )
    }
}
