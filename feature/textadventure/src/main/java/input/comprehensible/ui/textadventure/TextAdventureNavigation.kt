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
 * an existing one; [onSignInClick] is supplied by the host so the signed-out prompt can open the
 * account screen.
 */
fun NavGraphBuilder.textAdventureNavGraph(
    navController: NavController,
    onSignInClick: () -> Unit,
) {
    composable<TextAdventuresListRoute> {
        TextAdventuresListScreen(
            onSignInClick = onSignInClick,
            onStartAdventure = { navController.navigate(TextAdventureChatRoute()) },
            onAdventureClick = { adventureId ->
                navController.navigate(TextAdventureChatRoute(adventureId))
            },
        )
    }
    composable<TextAdventureChatRoute> { entry ->
        TextAdventureChatScreen(adventureId = entry.toRoute<TextAdventureChatRoute>().adventureId)
    }
}
