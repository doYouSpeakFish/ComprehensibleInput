package input.comprehensible.ui.settings.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

internal fun NavGraphBuilder.settingsScreen(
    onNavigateUp: () -> Unit,
    onGoToSoftwareLicences: () -> Unit,
) {
    composable<SettingsRoute> {
        Settings(
            onNavigateUp = onNavigateUp,
            onGoToSoftwareLicences = onGoToSoftwareLicences
        )
    }
}
