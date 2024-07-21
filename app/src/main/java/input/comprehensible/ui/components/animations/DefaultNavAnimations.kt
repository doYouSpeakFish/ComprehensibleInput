package input.comprehensible.ui.components.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

val defaultEnterTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(300),
    )
}

val defaultExitTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(300),
    )
}

val defaultPopEnterTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(300),
    )
}

val defaultPopExitTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(300),
    )
}
