package input.comprehensible.extensions

import androidx.window.core.layout.WindowSizeClass

val WindowSizeClass.isCompact: Boolean
    get() = !isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
