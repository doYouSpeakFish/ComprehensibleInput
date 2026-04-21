# src/main/kotlin/input/comprehensible/backend/textadventure/AdventureRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 9-23

Location: `src/main/kotlin/input/comprehensible/backend/textadventure/AdventureRepository.kt:9-23`

```kotlin
⚪    9 | }
⚪   10 | 
🔴   11 | data class PersistedAdventurePart(
🔴   12 |     val adventureId: String,
🔴   13 |     val title: String,
🔴   14 |     val learningLanguage: String,
🔴   15 |     val translationLanguage: String,
🔴   16 |     val isEnding: Boolean,
🔴   17 |     val paragraphs: List<PersistedAdventureParagraph>,
⚪   18 | )
⚪   19 | 
🔴   20 | data class PersistedAdventureParagraph(
🔴   21 |     val sentences: List<String>,
🔴   22 |     val translatedSentences: List<String>,
⚪   23 | )
```
