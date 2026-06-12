package input.comprehensible.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    bodyLarge = TextStyle.Default.copy(
        fontFamily = FontFamily.SansSerif,
        letterSpacing = 0.6.sp,
        fontSize = 20.sp,
        lineHeight = 28.0.sp,
        fontWeight = FontWeight.Normal,
    ),
)
