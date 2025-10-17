package input.comprehensible.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import input.comprehensible.analytics.LocalAnalyticsLogger
import input.comprehensible.analytics.logScreenView
import input.comprehensible.ui.settings.settings.SettingsRoute
import input.comprehensible.ui.settings.settingsNavGraph
import input.comprehensible.ui.storylist.StoryListRoute
import input.comprehensible.ui.storylist.storyList
import input.comprehensible.ui.storyreader.StoryReaderRoute
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
        DestinationChangeLogger(navController)
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = StoryListRoute,
        ) {
            settingsNavGraph(navController)
            storyReader()
            storyList(
                onStorySelected = { navController.navigate(StoryReaderRoute(storyId = it)) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
            )
        }
    }
}

@Composable
private fun DestinationChangeLogger(navController: NavHostController) {
    val analyticsLogger = LocalAnalyticsLogger.current
    DisposableEffect(navController, analyticsLogger) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route ?: return@OnDestinationChangedListener
            analyticsLogger.logScreenView(route)
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
