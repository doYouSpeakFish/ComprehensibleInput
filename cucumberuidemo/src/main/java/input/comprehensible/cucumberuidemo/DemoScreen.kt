package input.comprehensible.cucumberuidemo

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * A deliberately tiny Composable used to demonstrate that a Cucumber scenario can
 * drive a real Compose UI running under Robolectric.
 */
@Composable
fun DemoScreen() {
    var greeting by remember { mutableStateOf("Hello") }
    Column {
        Text(text = greeting, modifier = Modifier.testTag("greeting"))
        Button(
            onClick = { greeting = "Clicked" },
            modifier = Modifier.testTag("button"),
        ) {
            Text(text = "Tap me")
        }
    }
}
