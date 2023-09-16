package com.example.comprehensibleinput

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import com.example.home.HomeRoute
import com.example.home.homeNavGraph
import com.example.comprehensibleinput.ui.rememberAppState
import com.example.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val appState = rememberAppState()
                NavHost(
                    navController = appState.navController,
                    startDestination = HomeRoute
                ) {
                    homeNavGraph()
                }
            }
        }
    }
}
