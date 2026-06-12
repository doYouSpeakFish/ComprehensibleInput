package input.comprehensible.ui.settings.softwarelicences

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object SoftwareLicencesRoute

internal fun NavGraphBuilder.softwareLicences(
    onNavigateUp: () -> Unit
) {
    composable<SoftwareLicencesRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) {
        SoftwareLicences(
            onNavigateUp = onNavigateUp,
        )
    }
}
