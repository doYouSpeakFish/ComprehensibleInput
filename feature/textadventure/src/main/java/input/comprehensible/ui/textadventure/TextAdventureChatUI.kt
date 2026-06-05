package input.comprehensible.ui.textadventure

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * Placeholder for the text adventure chat screen. The list screen navigates here when an adventure
 * is opened or started; the screen itself is built out in a later increment.
 */
@Composable
internal fun TextAdventureChatScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.testTag("text_adventure_chat")) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "…")
        }
    }
}
