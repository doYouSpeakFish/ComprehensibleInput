package input.comprehensible.ui.components.error

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import input.comprehensible.common.R
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.ui.theme.backgroundDark
import input.comprehensible.util.DefaultPreview

const val GENERIC_ERROR_DIALOG_TEST_TAG = "generic_error_dialog"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericErrorDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .testTag(GENERIC_ERROR_DIALOG_TEST_TAG),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.Black),
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .widthIn(min = 280.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sad_robot),
                    contentDescription = stringResource(R.string.generic_error_dialog_content_description),
                    modifier = Modifier
                        .size(96.dp)
                        .border(width = 1.dp, color = backgroundDark, shape = CircleShape)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = stringResource(id = R.string.generic_error_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(id = R.string.generic_error_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Button(onClick = onDismissRequest) {
                    Text(text = stringResource(id = R.string.generic_error_dialog_button))
                }
            }
        }
    }
}

@DefaultPreview
@Composable
private fun PreviewGenericErrorDialog() {
    ComprehensibleInputTheme {
        GenericErrorDialog(onDismissRequest = {})
    }
}
