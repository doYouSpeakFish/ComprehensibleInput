package input.comprehensible.ui.textadventure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import input.comprehensible.feature.textadventure.R

/**
 * Displays an adventure's cover image, loaded from its URL with Coil. Renders nothing when there is
 * no image so callers can pass a nullable URL unconditionally.
 *
 * The image fills a sized [Box] that carries [testTag] and a placeholder background, so the slot
 * occupies its space (and stays identifiable in tests) while the image loads or if it fails to load.
 * [testTag] lets callers identify a specific image (a list row's thumbnail or the chat header).
 */
@Composable
internal fun AdventureImage(
    imageUrl: String?,
    testTag: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    if (imageUrl == null) return
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .testTag(testTag),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.text_adventure_image_content_description),
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
