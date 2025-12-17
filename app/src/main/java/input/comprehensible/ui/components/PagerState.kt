package input.comprehensible.ui.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Creates a [PagerState] that is remembered across compositions.
 *
 * @param initialPage The initial page to show when the pager is first created. If this value is
 * changed, the pager will not change the page.
 * @param pageToScrollTo The page to animate scroll to after the pager has been created. If this
 * value is changed to a non-null value, the pager will animate scroll to the new page.
 * @param onNewPageSettled A callback that is invoked when the pager has settled on a new page.
 * @param pageCount The number of pages in the pager.
 */
@Composable
fun rememberPagerState(
    initialPage: Int,
    pageToScrollTo: Int? = null,
    onNewPageSettled: (Int) -> Unit,
    pageCount: () -> Int,
): PagerState {
    val state = rememberPagerState(initialPage = initialPage, pageCount = pageCount)
    LaunchedEffect(state.settledPage) {
        onNewPageSettled(state.settledPage)
    }
    LaunchedEffect(pageToScrollTo) {
        if (pageToScrollTo != null) {
            state.animateScrollToPage(pageToScrollTo)
        }
    }
    return state
}
