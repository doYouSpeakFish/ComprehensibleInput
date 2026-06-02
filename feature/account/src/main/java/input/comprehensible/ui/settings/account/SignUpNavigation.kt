package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SignUpRoute

fun NavGraphBuilder.signUpScreen(
    onNavigateUp: () -> Unit,
    onAccountCreated: (email: String) -> Unit,
) {
    composable<SignUpRoute> {
        SignUpScreen(
            onNavigateUp = onNavigateUp,
            onAccountCreated = onAccountCreated,
        )
    }
}
