# src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 37-48

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:37-48`

```kotlin
⚪   37 |
⚪   38 |     private val textAdventuresFlow: Flow<TextAdventuresListResult> =
🟡   39 |         if (featureFlags.aiTextAdventuresEnabled) {
🟢   40 |             getTextAdventuresListUseCase()
⚪   41 |         } else {
🔴   42 |             flowOf(TextAdventuresListResult.Success(emptyList()))
⚪   43 |         }
⚪   44 |
```

## Lines 55-59

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:55-59`

```kotlin
⚪   55 |     ) { storiesResult, adventuresResult, learningLanguage, translationsLanguage ->
🟢   56 |         val storyItems = when (storiesResult) {
🟡   57 |             is StoriesListResult.Success -> storiesResult.storiesList.stories.map { story ->
🟢   58 |                 StoryListUiState.StoryListItem.Story(
🟢   59 |                     id = story.id,
```

## Lines 64-73

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:64-73`

```kotlin
⚪   64 |             }
⚪   65 |
🔴   66 |             StoriesListResult.Error -> emptyList()
⚪   67 |         }
🟢   68 |         val adventureItems = when (adventuresResult) {
🟡   69 |             is TextAdventuresListResult.Success -> adventuresResult.adventures.map { adventure ->
🟢   70 |                 StoryListUiState.StoryListItem.TextAdventure(
🟢   71 |                     id = adventure.id,
```

## Lines 75-83

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:75-83`

```kotlin
⚪   75 |             }
⚪   76 |
🔴   77 |             TextAdventuresListResult.Error -> emptyList()
⚪   78 |         }
🟢   79 |         val items = buildList {
🟢   80 |             addAll(storyItems)
🟡   81 |             if (featureFlags.aiTextAdventuresEnabled) {
🟢   82 |                 addAll(adventureItems)
🟢   83 |                 add(StoryListUiState.StoryListItem.StartTextAdventure)
```

## Lines 101-126

Location: `src/main/java/input/comprehensible/ui/storylist/StoryListViewModel.kt:101-126`

```kotlin
⚪  101 |     fun onLearningLanguageSelected(learningLanguage: LanguageSelection) {
🟢  102 |         viewModelScope.launch {
🟢  103 |             setLearningLanguageUseCase(learningLanguage.languageCode)
⚪  104 |         }
⚪  105 |     }
⚪  106 |
⚪  107 |     fun onTranslationLanguageSelected(translationLanguage: LanguageSelection) {
🟢  108 |         viewModelScope.launch {
🟢  109 |             setTranslationLanguageUseCase(translationLanguage.languageCode)
⚪  110 |         }
⚪  111 |     }
⚪  112 |
⚪  113 |     fun onStartTextAdventure() {
🟡  114 |         if (!featureFlags.aiTextAdventuresEnabled) return
🟢  115 |         val adventureId = startTextAdventureUseCase.generateAdventureId()
🟢  116 |         viewModelScope.launch {
🟢  117 |             _events.emit(StoryListEvent.TextAdventureStarted(adventureId))
🟢  118 |             try {
🟢  119 |                 startTextAdventureUseCase(adventureId)
🔴  120 |             } catch (e: CancellationException) {
🔴  121 |                 throw e
🟢  122 |             } catch (e: Exception) {
🟢  123 |                 Timber.e(e, "Failed to start text adventure %s", adventureId)
⚪  124 |             }
⚪  125 |         }
⚪  126 |     }
```
