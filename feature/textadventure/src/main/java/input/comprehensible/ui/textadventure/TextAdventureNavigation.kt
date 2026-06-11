package input.comprehensible.ui.textadventure

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data object TextAdventuresListRoute

@Serializable
data class TextAdventureChatRoute(val adventureId: String? = null)

/**
 * Registers the text adventure destinations. The list opens the chat for a new adventure (no id) or
 * an existing one; [onSignInClick], [onCreateAccountClick] and [onSettingsClick] are supplied by the
 * host so the screen can open the account and settings destinations.
 */
fun NavGraphBuilder.textAdventureNavGraph(
    navController: NavController,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable<TextAdventuresListRoute> {
        TextAdventuresListScreen(
            onSignInClick = onSignInClick,
            onCreateAccountClick = onCreateAccountClick,
            onStartAdventure = { navController.navigate(TextAdventureChatRoute()) },
            onAdventureClick = { adventureId ->
                navController.navigate(TextAdventureChatRoute(adventureId))
            },
            onSettingsClick = onSettingsClick,
        )
    }
    composable<TextAdventureChatRoute> { entry ->
        TextAdventureChatScreen(adventureId = entry.toRoute<TextAdventureChatRoute>().adventureId)
    }
}
