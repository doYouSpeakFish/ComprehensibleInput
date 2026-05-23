# src/main/java/input/comprehensible/usecases/StartTextAdventureUseCase.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 15-23

Location: `src/main/java/input/comprehensible/usecases/StartTextAdventureUseCase.kt:15-23`

```kotlin
⚪   15 |     @OptIn(ExperimentalCoroutinesApi::class)
⚪   16 |     suspend operator fun invoke(): String {
🔴   17 |         val learningLanguage = languageSettingsRepository.learningLanguage.first()
🔴   18 |         val translationsLanguage = languageSettingsRepository.translationsLanguage.first()
🔴   19 |         return repository.startNewAdventure(
🔴   20 |             learningLanguage = learningLanguage,
🔴   21 |             translationsLanguage = translationsLanguage,
⚪   22 |         )
⚪   23 |     }
```
