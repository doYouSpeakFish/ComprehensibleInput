package input.comprehensible.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    textAdventuresEnabled: Boolean,
    onStoriesClick: () -> Unit,
    onTextAdventuresClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            textAdventuresEnabled = textAdventuresEnabled,
            onStoriesClick = onStoriesClick,
            onTextAdventuresClick = onTextAdventuresClick,
            onSettingsClick = onSettingsClick,
        )
    }
}
