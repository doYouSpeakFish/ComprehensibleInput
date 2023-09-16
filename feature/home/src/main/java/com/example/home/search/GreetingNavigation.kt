package com.example.home.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val GreetingRoute = "greeting"

fun NavController.navigateToGreeting(navOptions: NavOptions? = null) {
    this.navigate(GreetingRoute, navOptions)
}

fun NavGraphBuilder.greetingScreen() {
    composable(GreetingRoute) {
        Greeting()
    }
}
