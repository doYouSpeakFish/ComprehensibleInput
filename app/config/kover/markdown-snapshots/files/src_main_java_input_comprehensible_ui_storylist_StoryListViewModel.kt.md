# src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 42-46

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:42-46`

```kotlin
⚪   42 |     ) { storiesResult, adventuresResult, learningLanguage, translationsLanguage ->
🟢   43 |         val storyItems = when (storiesResult) {
🟡   44 |             is StoriesListResult.Success -> storiesResult.storiesList.stories.map { story ->
🟢   45 |                 StoryListUiState.StoryListItem.Story(
🟢   46 |                     id = story.id,
```

## Lines 51-58

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:51-58`

```kotlin
⚪   51 |             }
⚪   52 | 
🔴   53 |             StoriesListResult.Error -> emptyList()
⚪   54 |         }
🟢   55 |         val adventureItems = when (adventuresResult) {
🟡   56 |             is TextAdventuresListResult.Success -> adventuresResult.adventures.map { adventure ->
🟢   57 |                 StoryListUiState.StoryListItem.TextAdventure(
🟢   58 |                     id = adventure.id,
```

## Lines 62-66

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:62-66`

```kotlin
⚪   62 |             }
⚪   63 | 
🔴   64 |             TextAdventuresListResult.Error -> emptyList()
⚪   65 |         }
🟢   66 |         val items = buildList {
```

## Lines 105-109

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:105-109`

```kotlin
⚪  105 | 
⚪  106 |     fun onStartTextAdventure() {
🟡  107 |         if (!featureFlags.aiTextAdventuresEnabled) return
🟢  108 |         viewModelScope.launch {
🟢  109 |             val adventureId = startTextAdventureUseCase()
```
