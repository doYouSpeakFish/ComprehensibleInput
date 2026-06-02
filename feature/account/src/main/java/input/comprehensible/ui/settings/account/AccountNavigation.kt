package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object AccountRoute

fun NavGraphBuilder.accountScreen(
    onNavigateUp: () -> Unit,
    onGoToSignUp: () -> Unit,
) {
    composable<AccountRoute> {
        AccountScreen(
            onNavigateUp = onNavigateUp,
            onGoToSignUp = onGoToSignUp,
        )
    }
}
