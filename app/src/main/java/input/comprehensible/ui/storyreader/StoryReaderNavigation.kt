package input.comprehensible.ui.storyreader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val BASE_ROUTE = "storyReader"
private const val STORY_ID_NAV_KEY = "storyId"

const val STORY_READER_ROUTE = "$BASE_ROUTE/{$STORY_ID_NAV_KEY}"

/**
 * Navigate to the story reader screen.
 */
fun NavController.navigateToStoryReader(
    storyId: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = "$BASE_ROUTE/$storyId",
        builder = builder
    )
}

/**
 * Add the story reader screen to the navigation graph.
 */
fun NavGraphBuilder.storyReader() {
    composable(
        route = STORY_READER_ROUTE,
        arguments = listOf(
            navArgument(STORY_ID_NAV_KEY) {
                type = NavType.StringType
            }
        )
    ) {
        StoryReader(Modifier.fillMaxSize())
    }
}
