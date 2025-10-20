package input.comprehensible.ui.settings.softwarelicences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import input.comprehensible.R
import input.comprehensible.ui.components.topbar.SettingsTopBar
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@Composable
internal fun SoftwareLicences(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(
                title = stringResource(R.string.software_licences_screen_title),
                onNavigateUp = onNavigateUp,
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            val libraries by produceLibraries(R.raw.aboutlibraries)
            LibrariesContainer(
                modifier = Modifier.fillMaxSize(),
                libraries = libraries,
            )
        }
    }
}

@DefaultPreview
@Composable
fun PreviewSoftwareLicences() {
    ComprehensibleInputTheme {
        SoftwareLicences(
            onNavigateUp = {}
        )
    }
}
