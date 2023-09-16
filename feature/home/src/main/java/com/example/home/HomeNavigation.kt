package com.example.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import com.example.home.search.GreetingRoute
import com.example.home.search.greetingScreen

const val HomeRoute = "home"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(HomeRoute, navOptions)
}

fun NavGraphBuilder.homeNavGraph() {
    navigation(
        startDestination = GreetingRoute,
        route = HomeRoute
    ) {
        greetingScreen()
    }
}
