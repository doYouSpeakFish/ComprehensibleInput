package input.comprehensible.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import input.comprehensible.ui.home.HomeRoute
import input.comprehensible.ui.home.homeScreen
import input.comprehensible.ui.settings.account.AccountRoute
import input.comprehensible.ui.settings.settings.SettingsRoute
import input.comprehensible.ui.settings.settingsNavGraph
import input.comprehensible.ui.storylist.StoryListRoute
import input.comprehensible.ui.storylist.storyList
import input.comprehensible.ui.storyreader.StoryReaderRoute
import input.comprehensible.ui.storyreader.storyReader
import input.comprehensible.ui.textadventure.TextAdventuresListRoute
import input.comprehensible.ui.textadventure.textAdventureNavGraph
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.FeatureFlags

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
            startDestination = HomeRoute,
        ) {
            homeScreen(
                textAdventuresEnabled = FeatureFlags().aiTextAdventuresEnabled,
                onStoriesClick = { navController.navigate(StoryListRoute) },
                onTextAdventuresClick = { navController.navigate(TextAdventuresListRoute) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
            )
            settingsNavGraph(navController)
            storyReader(
                onErrorDismissed = { navController.popBackStack() }
            )
            storyList(
                onStorySelected = { navController.navigate(StoryReaderRoute(storyId = it)) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
            )
            textAdventureNavGraph(
                navController = navController,
                onSignInClick = { navController.navigate(AccountRoute) },
                onCreateAccountClick = { navController.navigate(AccountRoute) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
            )
        }
    }
}
