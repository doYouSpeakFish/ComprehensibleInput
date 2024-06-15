package input.comprehensible.ui.settings.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import input.comprehensible.R
import input.comprehensible.ui.components.topbar.SettingsTopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
internal fun Settings(
    onNavigateUp: () -> Unit,
    onGoToSoftwareLicences: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(
                title = stringResource(R.string.settings_screen_title),
                onNavigateUp = onNavigateUp,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            SettingsItem(
                onClick = onGoToSoftwareLicences,
                title = stringResource(R.string.settings_item_software_licences)
            )
        }
    }
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    title: String,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        )
    ) {
        Column {
            Row(Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
            HorizontalDivider()
        }
    }
}

@DefaultPreview
@Composable
fun SettingsPreview() {
    ComprehensibleInputTheme {
        Settings(
            modifier = Modifier.fillMaxSize(),
            onNavigateUp = {},
            onGoToSoftwareLicences = {}
        )
    }
}
