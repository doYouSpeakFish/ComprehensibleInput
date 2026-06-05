package input.comprehensible.ui.textadventure

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TextAdventuresListRoute

@Serializable
data object TextAdventureChatRoute

/**
 * Registers the text adventure destinations. Navigation from the list into the chat is internal;
 * [onSignInClick] is supplied by the host so the signed-out prompt can open the account screen.
 */
fun NavGraphBuilder.textAdventureNavGraph(
    navController: NavController,
    onSignInClick: () -> Unit,
) {
    composable<TextAdventuresListRoute> {
        TextAdventuresListScreen(
            onSignInClick = onSignInClick,
            onStartAdventure = { navController.navigate(TextAdventureChatRoute) },
        )
    }
    composable<TextAdventureChatRoute> {
        TextAdventureChatScreen()
    }
}
