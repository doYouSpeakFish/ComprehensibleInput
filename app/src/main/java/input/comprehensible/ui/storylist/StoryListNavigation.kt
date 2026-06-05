package input.comprehensible.ui.storylist

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
data object StoryListRoute

/**
 * Adds the story list screen to the navigation graph.
 */
fun NavGraphBuilder.storyList(
    onStorySelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable<StoryListRoute>(
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition,
    ) {
        StoryListScreen(
            modifier = Modifier.fillMaxSize(),
            onStorySelected = onStorySelected,
            onSettingsClick = onSettingsClick,
        )
    }
}
