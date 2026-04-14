# src/main/java/input/comprehensible/data/stories/StoriesRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 61-71

Location: `src/main/java/input/comprehensible/data/stories/StoriesRepository.kt:61-71`

```kotlin
🟢   61 |             StoriesListResult.Success(StoriesList(stories = storyListItems))
🟢   62 |         }.catch { throwable ->
🔴   63 |             Timber.e(
🔴   64 |                 throwable,
🔴   65 |                 "Failed to load stories list for learning language %s and translations language %s",
🔴   66 |                 learningLanguage,
🔴   67 |                 translationsLanguage,
⚪   68 |             )
🔴   69 |             emit(StoriesListResult.Error)
⚪   70 |     }
⚪   71 | 
```

## Lines 163-171

Location: `src/main/java/input/comprehensible/data/stories/StoriesRepository.kt:163-171`

```kotlin
⚪  163 | 
🟢  164 |         val parts = path.mapIndexed { i, partId ->
🟡  165 |             val learningPart = requireNotNull(partsById[partId]) {
🔴  166 |                 "Story $id is missing part with id $partId"
⚪  167 |             }
🟡  168 |             val translationPart = requireNotNull(translation.partsById[partId]) {
🔴  169 |                 "Story $id translation is missing part with id $partId"
⚪  170 |             }
🟢  171 |             val nextPartId = path.getOrNull(i + 1)
```

## Lines 199-203

Location: `src/main/java/input/comprehensible/data/stories/StoriesRepository.kt:199-203`

```kotlin
🟢  199 |             while (nextPartId != null) {
🟢  200 |                 add(nextPartId)
🟡  201 |                 nextPartId = partsById[nextPartId]?.choice?.parentPartId
⚪  202 |             }
🟢  203 |         }.reversed()
```

## Lines 216-227

Location: `src/main/java/input/comprehensible/data/stories/StoriesRepository.kt:216-227`

```kotlin
🟢  216 |         val translatedChildrenById = translatedChildren.associateBy { it.id }
🟢  217 |         val choicesWithTranslations = children.map { childPart ->
🟡  218 |             val translatedChild = requireNotNull(translatedChildrenById[childPart.id]) {
🔴  219 |                 "Story $id translation is missing part with id ${childPart.id}"
⚪  220 |             }
🟡  221 |             val choice = requireNotNull(childPart.choice) {
🔴  222 |                 "Story $id child part ${childPart.id} is missing its parent choice"
⚪  223 |             }
🟡  224 |             val translatedChoice = requireNotNull(translatedChild.choice) {
🔴  225 |                 "Story $id translation is missing parent choice for part ${childPart.id}"
⚪  226 |             }
🟢  227 |             choice.toStoryChoice(
```

## Lines 232-237

Location: `src/main/java/input/comprehensible/data/stories/StoriesRepository.kt:232-237`

```kotlin
⚪  232 |         }
⚪  233 | 
🟡  234 |         require(content.size == translation.content.size) {
🔴  235 |             "Story $id content could not be fully matched between $learningLanguage and $translationsLanguage"
⚪  236 |         }
🟢  237 |         val elements = content.zip(translation.content) { elementData, translation ->
```

## Lines 275-280

Location: `src/main/java/input/comprehensible/data/stories/StoriesRepository.kt:275-280`

```kotlin
🟢  275 |         return when (this) {
🟢  276 |             is StoryElementData.ParagraphData -> {
🟡  277 |                 val translationParagraph = translation as? StoryElementData.ParagraphData ?: run {
🔴  278 |                     Timber.e("No matching translation found for paragraph in story $storyId")
🟢  279 |                     return null
⚪  280 |                 }
```

## Lines 342-346

Location: `src/main/java/input/comprehensible/data/stories/StoriesRepository.kt:342-346`

```kotlin
⚪  342 | sealed interface StoriesListResult {
🟢  343 |     data class Success(val storiesList: StoriesList) : StoriesListResult
🔴  344 |     object Error : StoriesListResult
⚪  345 | }
⚪  346 | 
```
