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

## Lines 51-70

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:51-70`

```kotlin
⚪   51 |             }
⚪   52 | 
🔴   53 |             StoriesListResult.Error -> emptyList()
⚪   54 |         }
🟢   55 |         val adventureItems = when (adventuresResult) {
🟡   56 |             is TextAdventuresListResult.Success -> adventuresResult.adventures.map { adventure ->
🔴   57 |                 StoryListUiState.StoryListItem.TextAdventure(
🔴   58 |                     id = adventure.id,
🔴   59 |                     title = adventure.title,
🔴   60 |                     isComplete = adventure.isComplete,
🔴   61 |                 )
⚪   62 |             }
⚪   63 | 
🔴   64 |             TextAdventuresListResult.Error -> emptyList()
⚪   65 |         }
🟢   66 |         val items = buildList {
🟢   67 |             addAll(storyItems)
🟡   68 |             if (featureFlags.aiTextAdventuresEnabled) {
🟢   69 |                 addAll(adventureItems)
🟢   70 |                 add(StoryListUiState.StoryListItem.StartTextAdventure)
```

## Lines 105-112

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:105-112`

```kotlin
⚪  105 | 
⚪  106 |     fun onStartTextAdventure() {
🔴  107 |         if (!featureFlags.aiTextAdventuresEnabled) return
🔴  108 |         viewModelScope.launch {
🔴  109 |             val adventureId = startTextAdventureUseCase()
🔴  110 |             _events.emit(StoryListEvent.TextAdventureStarted(adventureId))
⚪  111 |         }
⚪  112 |     }
```

## Lines 114-117

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:114-117`

```kotlin
⚪  114 | 
⚪  115 | sealed interface StoryListEvent {
🔴  116 |     data class TextAdventureStarted(val adventureId: String) : StoryListEvent
⚪  117 | }
```
