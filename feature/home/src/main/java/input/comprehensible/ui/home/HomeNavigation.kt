package input.comprehensible.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    textAdventuresEnabled: Boolean,
    onStoriesClick: () -> Unit,
    onTextAdventuresClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable<HomeRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) {
        HomeScreen(
            textAdventuresEnabled = textAdventuresEnabled,
            onStoriesClick = onStoriesClick,
            onTextAdventuresClick = onTextAdventuresClick,
            onSettingsClick = onSettingsClick,
        )
    }
}
