# src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 33-40

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:33-40`

```kotlin
⚪   33 |
⚪   34 |     private val textAdventuresFlow: Flow<TextAdventuresListResult> =
🟡   35 |         if (featureFlags.aiTextAdventuresEnabled) {
🟢   36 |             getTextAdventuresListUseCase()
⚪   37 |         } else {
🔴   38 |             flowOf(TextAdventuresListResult.Success(emptyList()))
⚪   39 |         }
⚪   40 |
```

## Lines 45-49

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:45-49`

```kotlin
⚪   45 |     ) { storiesResult, adventuresResult, learningLanguage, translationsLanguage ->
🟢   46 |         val storyItems = when (storiesResult) {
🟡   47 |             is StoriesListResult.Success -> storiesResult.storiesList.stories.map { story ->
🟢   48 |                 StoryListUiState.StoryListItem.Story(
🟢   49 |                     id = story.id,
```

## Lines 54-61

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:54-61`

```kotlin
⚪   54 |             }
⚪   55 |
🔴   56 |             StoriesListResult.Error -> emptyList()
⚪   57 |         }
🟢   58 |         val adventureItems = when (adventuresResult) {
🟡   59 |             is TextAdventuresListResult.Success -> adventuresResult.adventures.map { adventure ->
🟢   60 |                 StoryListUiState.StoryListItem.TextAdventure(
🟢   61 |                     id = adventure.id,
```

## Lines 65-73

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:65-73`

```kotlin
⚪   65 |             }
⚪   66 |
🔴   67 |             TextAdventuresListResult.Error -> emptyList()
⚪   68 |         }
🟢   69 |         val items = buildList {
🟢   70 |             addAll(storyItems)
🟡   71 |             if (featureFlags.aiTextAdventuresEnabled) {
🟢   72 |                 addAll(adventureItems)
🟢   73 |                 add(StoryListUiState.StoryListItem.StartTextAdventure)
```

## Lines 87-104

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:87-104`

```kotlin
⚪   87 |     fun onLearningLanguageSelected(learningLanguage: LanguageSelection) {
🟢   88 |         viewModelScope.launch {
🟢   89 |             setLearningLanguageUseCase(learningLanguage.languageCode)
⚪   90 |         }
⚪   91 |     }
⚪   92 |
⚪   93 |     fun onTranslationLanguageSelected(translationLanguage: LanguageSelection) {
🟢   94 |         viewModelScope.launch {
🟢   95 |             setTranslationLanguageUseCase(translationLanguage.languageCode)
⚪   96 |         }
⚪   97 |     }
⚪   98 |
⚪   99 |     fun onStartTextAdventure() {
🟡  100 |         if (!featureFlags.aiTextAdventuresEnabled) return
🟢  101 |         val adventureId = startTextAdventureUseCase.generateAdventureId()
🟢  102 |         viewModelScope.launch {
🟢  103 |             _events.emit(StoryListEvent.TextAdventureStarted(adventureId))
🟢  104 |             startTextAdventureUseCase(adventureId)
```
