package com.example.home.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AppTheme

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    viewModel: GreetingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle(initialValue = GreetingUiState.INITIAL)
    Greeting(
        modifier = modifier,
        uiState = state
    )
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    uiState: GreetingUiState
) {
    Box(modifier) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Text(text = "Hello ${uiState.usersName}!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting(
            uiState = GreetingUiState(
                usersName = "Android"
            )
        )
    }
}
