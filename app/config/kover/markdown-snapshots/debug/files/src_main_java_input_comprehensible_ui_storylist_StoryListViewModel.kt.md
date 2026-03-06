# src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 34-41

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:34-41`

```kotlin
⚪   34 |
⚪   35 |     private val textAdventuresFlow: Flow<TextAdventuresListResult> =
🟡   36 |         if (featureFlags.aiTextAdventuresEnabled) {
🟢   37 |             getTextAdventuresListUseCase()
⚪   38 |         } else {
🔴   39 |             flowOf(TextAdventuresListResult.Success(emptyList()))
⚪   40 |         }
⚪   41 |
```

## Lines 47-51

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:47-51`

```kotlin
⚪   47 |     ) { storiesResult, adventuresResult, learningLanguage, translationsLanguage ->
🟢   48 |         val storyItems = when (storiesResult) {
🟡   49 |             is StoriesListResult.Success -> storiesResult.storiesList.stories.map { story ->
🟢   50 |                 StoryListUiState.StoryListItem.Story(
🟢   51 |                     id = story.id,
```

## Lines 56-63

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:56-63`

```kotlin
⚪   56 |             }
⚪   57 |
🔴   58 |             StoriesListResult.Error -> emptyList()
⚪   59 |         }
🟢   60 |         val adventureItems = when (adventuresResult) {
🟡   61 |             is TextAdventuresListResult.Success -> adventuresResult.adventures.map { adventure ->
🟢   62 |                 StoryListUiState.StoryListItem.TextAdventure(
🟢   63 |                     id = adventure.id,
```

## Lines 67-75

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:67-75`

```kotlin
⚪   67 |             }
⚪   68 |
🔴   69 |             TextAdventuresListResult.Error -> emptyList()
⚪   70 |         }
🟢   71 |         val items = buildList {
🟢   72 |             addAll(storyItems)
🟡   73 |             if (featureFlags.aiTextAdventuresEnabled) {
🟢   74 |                 addAll(adventureItems)
🟢   75 |                 add(StoryListUiState.StoryListItem.StartTextAdventure)
```

## Lines 113-124

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:113-124`

```kotlin
⚪  113 |     fun onStartTextAdventure() {
🟡  114 |         if (!featureFlags.aiTextAdventuresEnabled) return
🟢  115 |         val adventureId = startTextAdventureUseCase.generateAdventureId()
🟢  116 |         viewModelScope.launch {
🟢  117 |             _events.emit(StoryListEvent.TextAdventureStarted(adventureId))
⚪  118 |             try {
🟢  119 |                 startTextAdventureUseCase(adventureId)
🟡  120 |             } catch (e: Exception) {
🟢  121 |                 ensureActive()
🟢  122 |                 Timber.e(e, "Failed to start text adventure %s", adventureId)
⚪  123 |             }
⚪  124 |         }
```
