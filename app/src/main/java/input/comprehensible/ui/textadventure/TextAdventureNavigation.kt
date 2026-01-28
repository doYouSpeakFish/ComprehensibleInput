package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object TextAdventureRoute

fun NavGraphBuilder.textAdventure(
    onNavigateUp: () -> Unit,
) {
    composable<TextAdventureRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) {
        TextAdventureScreen(
            modifier = Modifier.fillMaxSize(),
            onNavigateUp = onNavigateUp,
        )
    }
}
