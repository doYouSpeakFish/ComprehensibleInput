package input.comprehensible.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import input.comprehensible.common.R

/**
 * A pill that tells the reader they can tap any sentence to translate it.
 *
 * Tapping it cycles through a series of increasingly playful messages (an easter egg):
 * [timesExplainerTapped] indexes into [R.array.tap_to_translate_explainer] and [onExplainerTapped]
 * advances it. Tapping is disabled once the final message is reached. Shared by the story reader and
 * the text adventure chat so the notice looks and behaves the same in both.
 */
@Composable
fun TranslateExplainer(
    modifier: Modifier = Modifier,
    onExplainerTapped: () -> Unit,
    timesExplainerTapped: Int,
) {
    val explainerMessages =
        stringArrayResource(id = R.array.tap_to_translate_explainer)
    Box(modifier, propagateMinConstraints = true) {
        Text(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape
                )
                .padding(vertical = 4.dp, horizontal = 12.dp)
                .clickable(
                    onClick = onExplainerTapped,
                    enabled = timesExplainerTapped < explainerMessages.lastIndex,
                )
                .animateContentSize(),
            text = explainerMessages.getOrElse(timesExplainerTapped) { explainerMessages.last() },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.background,
        )
    }
}
