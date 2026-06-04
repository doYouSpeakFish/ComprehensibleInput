package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DeleteAccountRoute

fun NavGraphBuilder.deleteAccountScreen(
    onNavigateUp: () -> Unit,
    onAccountDeleted: () -> Unit,
) {
    composable<DeleteAccountRoute> {
        DeleteAccountScreen(
            onNavigateUp = onNavigateUp,
            onAccountDeleted = onAccountDeleted,
        )
    }
}
