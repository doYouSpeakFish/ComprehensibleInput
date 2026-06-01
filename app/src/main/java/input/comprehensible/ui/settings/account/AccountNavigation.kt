package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object AccountRoute

internal fun NavGraphBuilder.accountScreen(
    onNavigateUp: () -> Unit,
) {
    composable<AccountRoute> {
        AccountScreen(
            onNavigateUp = onNavigateUp,
        )
    }
}
