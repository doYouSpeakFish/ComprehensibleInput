# src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported


## Lines 25-29

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:25-29`

```kotlin
⚪   25 |     ) { storiesResult, learningLanguage, translationsLanguage ->
🟢   26 |         val stories = when (storiesResult) {
🟡   27 |             is StoriesListResult.Success -> storiesResult.storiesList.stories.map { story ->
🟢   28 |                 StoryListUiState.StoryListItem(
🟢   29 |                     id = story.id,
```

## Lines 34-38

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:34-38`

```kotlin
⚪   34 |             }
⚪   35 | 
🔴   36 |             StoriesListResult.Error -> emptyList()
⚪   37 |         }
🟢   38 |         StoryListUiState(
```

