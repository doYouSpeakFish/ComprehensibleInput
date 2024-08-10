package input.comprehensible.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import input.comprehensible.ui.settings.settings.navigateToSettings
import input.comprehensible.ui.settings.settingsNavGraph
import input.comprehensible.ui.storylist.STORY_LIST_ROUTE
import input.comprehensible.ui.storylist.storyList
import input.comprehensible.ui.storyreader.navigateToStoryReader
import input.comprehensible.ui.storyreader.storyReader
import input.comprehensible.ui.theme.ComprehensibleInputTheme

/**
 * The root composable function for the app.
 */
@Composable
fun ComprehensibleInputApp(
    navController: NavHostController = rememberNavController()
) {
    ComprehensibleInputTheme {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = STORY_LIST_ROUTE,
        ) {
            settingsNavGraph(navController)
            storyReader(navController)
            storyList(
                onStorySelected = navController::navigateToStoryReader,
                onSettingsClick = navController::navigateToSettings,
            )
        }
    }
}
