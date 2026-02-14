package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data class TextAdventureRoute(
    val adventureId: String,
)

fun NavGraphBuilder.textAdventure(
    onNavigateUp: () -> Unit,
) {
    composable<TextAdventureRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) { backStackEntry ->
        val args = backStackEntry.toRoute<TextAdventureRoute>()
        TextAdventureScreen(
            modifier = Modifier.fillMaxSize(),
            adventureId = args.adventureId,
            onNavigateUp = onNavigateUp,
        )
    }
}
