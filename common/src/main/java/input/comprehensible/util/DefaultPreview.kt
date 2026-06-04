package input.comprehensible.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Multi-preview annotation that renders a composable in the six screens we care about for
 * screenshot testing. These are deliberately kept as six distinct, single-variable previews
 * (rather than the cross product of device/font/theme/locale) so that each preview produces
 * exactly one screenshot:
 *
 *  - normal phone - portrait
 *  - landscape tablet
 *  - portrait tablet
 *  - phone very large font
 *  - phone dark theme
 *  - phone german language
 */
@Target(FUNCTION)
@Retention(BINARY)
@Preview(
    name = "normal phone - portrait",
    showBackground = true,
)
@Preview(
    name = "landscape tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
@Preview(
    name = "portrait tablet",
    showBackground = true,
    device = "spec:width=800dp,height=1280dp,dpi=240",
)
@Preview(
    name = "phone very large font",
    showBackground = true,
    fontScale = 2f,
)
@Preview(
    name = "phone dark theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Preview(
    name = "phone german language",
    showBackground = true,
    locale = "de",
)
annotation class DefaultPreview
