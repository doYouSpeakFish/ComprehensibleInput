package input.comprehensible.ui.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ForgotPasswordRoute

fun NavGraphBuilder.forgotPasswordScreen(
    onNavigateUp: () -> Unit,
    onCodeSent: (email: String) -> Unit,
) {
    composable<ForgotPasswordRoute> {
        ForgotPasswordScreen(
            onNavigateUp = onNavigateUp,
            onCodeSent = onCodeSent,
        )
    }
}
