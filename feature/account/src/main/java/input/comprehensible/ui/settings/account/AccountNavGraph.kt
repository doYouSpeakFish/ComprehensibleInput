package input.comprehensible.ui.settings.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

fun NavGraphBuilder.accountNavGraph(navController: NavController) {
    accountScreen(
        onNavigateUp = navController::navigateUp,
        onGoToSignUp = { navController.navigate(SignUpRoute) },
    )
    signUpScreen(
        onNavigateUp = navController::navigateUp,
        onAccountCreated = { email -> navController.navigate(VerifyEmailRoute(email)) },
    )
    verifyEmailScreen(
        onNavigateUp = navController::navigateUp,
        onVerified = { navController.popBackStack(AccountRoute, inclusive = false) },
    )
}
