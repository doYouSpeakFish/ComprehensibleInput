package input.comprehensible.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
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
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComprehensibleInputApp(
    navController: NavHostController = rememberNavController()
) {
    ComprehensibleInputTheme {
        Surface {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = STORY_LIST_ROUTE,
                    ) {
                        settingsNavGraph(navController)
                        storyReader()
                        storyList(
                            onStorySelected = navController::navigateToStoryReader,
                            onSettingsClick = navController::navigateToSettings,
                        )
                    }
                }
            }
        }
    }
}

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
