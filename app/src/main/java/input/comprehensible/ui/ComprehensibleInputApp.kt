package input.comprehensible.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import input.comprehensible.ui.storyreader.StoryReader
import input.comprehensible.ui.theme.ComprehensibleInputTheme

/**
 * The root composable function for the app.
 */
@Composable
fun ComprehensibleInputApp(
    navController: NavHostController = rememberNavController()
) {
    ComprehensibleInputTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = "storyReader/{storyId}"
            ) {
                composable(
                    route = "storyReader/{storyId}",
                    arguments = listOf(
                        navArgument("storyId") {
                            type = NavType.StringType
                            defaultValue = "1"
                        }
                    )
                ) {
                    StoryReader(Modifier.fillMaxSize())
                }
            }
        }
    }
}
