# app/src/main/java/input/comprehensible/ui/components/PagerState.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported


## Lines 24-32

Location: `app/src/main/java/input/comprehensible/ui/components/PagerState.kt:24-32`

```kotlin
🟢   24 | ): PagerState {
🟢   25 |     val state = rememberPagerState(initialPage = initialPage, pageCount = pageCount)
🟡   26 |     LaunchedEffect(state.settledPage) {
🟢   27 |         onNewPageSettled(state.settledPage)
⚪   28 |     }
🟡   29 |     LaunchedEffect(pageToScrollTo) {
🟡   30 |         if (pageToScrollTo != null && pageToScrollTo >= 0 && pageToScrollTo < pageCount()) {
🟢   31 |             state.animateScrollToPage(pageToScrollTo)
⚪   32 |         }
```

