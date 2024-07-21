package input.comprehensible.ui.storylist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import input.comprehensible.ui.components.animations.defaultEnterTransition
import input.comprehensible.ui.components.animations.defaultExitTransition
import input.comprehensible.ui.components.animations.defaultPopEnterTransition
import input.comprehensible.ui.components.animations.defaultPopExitTransition

const val STORY_LIST_ROUTE = "storyList"

/**
 * Navigates to the story list screen.
 */
fun NavController.navigateToStoryList(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = STORY_LIST_ROUTE,
        builder = builder
    )
}

/**
 * Adds the story list screen to the navigation graph.
 */
fun NavGraphBuilder.storyList(
    onStorySelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable(
        route = "StoryList",
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
