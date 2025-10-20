package input.comprehensible.util

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(BINARY)
@PreviewFontScale
@PreviewScreenSizes
@PreviewLightDark
@Preview(showBackground = true)
annotation class DefaultPreview

