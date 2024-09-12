package input.comprehensible.ui.settings.softwarelicences

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SoftwareLicencesRoute

internal fun NavGraphBuilder.softwareLicences(
    onNavigateUp: () -> Unit
) {
    composable<SoftwareLicencesRoute> {
        SoftwareLicences(
            onNavigateUp = onNavigateUp,
        )
    }
}
