package com.example.comprehensibleinput.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * A state holder for app wide state.
 */
data class AppState(
    val navController: NavHostController
)

/**
 * Creates and remembers an [AppState].
 */
@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController()
): AppState = remember(navController) {
    AppState(
        navController = navController
    )
}
