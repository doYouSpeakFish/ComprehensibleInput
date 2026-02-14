package input.comprehensible.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import input.comprehensible.ui.settings.settings.SettingsRoute
import input.comprehensible.ui.settings.settingsNavGraph
import input.comprehensible.ui.storylist.StoryListRoute
import input.comprehensible.ui.storylist.storyList
import input.comprehensible.ui.storyreader.StoryReaderRoute
import input.comprehensible.ui.storyreader.storyReader
import input.comprehensible.ui.textadventure.TextAdventureRoute
import input.comprehensible.ui.textadventure.textAdventure
import input.comprehensible.ui.theme.ComprehensibleInputTheme

/**
 * The root composable function for the app.
 */
@Composable
fun ComprehensibleInputApp(
    navController: NavHostController,
    darkTheme: Boolean,
) {
    ComprehensibleInputTheme(darkTheme = darkTheme) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = StoryListRoute,
        ) {
            settingsNavGraph(navController)
            storyReader(
                onErrorDismissed = { navController.popBackStack() }
            )
            storyList(
                onStorySelected = { navController.navigate(StoryReaderRoute(storyId = it)) },
                onTextAdventureSelected = { navController.navigate(TextAdventureRoute(adventureId = it)) },
                onTextAdventureStarted = { navController.navigate(TextAdventureRoute(adventureId = it)) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
            )
            textAdventure(onNavigateUp = { navController.popBackStack() })
        }
    }
}
