package `in`.comprehensible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.comprehensible.ui.theme.ComprehensibleInputTheme
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        Timber.d("Setting UI content")

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ComprehensibleInputTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = "greeting"
                    ) {
                        composable("greeting") {
                            Greeting()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    viewModel: GreetingViewModel = hiltViewModel()
) {
    Text(
        modifier = modifier,
        text = "Hello ${viewModel.name}!"
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComprehensibleInputTheme {
        Greeting()
    }
}

@HiltViewModel
class GreetingViewModel @Inject constructor() : ViewModel() {
    val name = "Android"
}
