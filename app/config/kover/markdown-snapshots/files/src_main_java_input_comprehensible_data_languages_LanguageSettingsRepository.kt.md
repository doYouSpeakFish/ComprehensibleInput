# src/main/java/input/comprehensible/data/languages/LanguageSettingsRepository.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 26-32

Location: `src/main/java/input/comprehensible/data/languages/LanguageSettingsRepository.kt:26-32`

```kotlin
⚪   26 |     suspend fun setLearningLanguage(language: String) {
🟢   27 |         val translationLanguage = translationsLanguage.first()
🟡   28 |         if (language == translationLanguage) {
🔴   29 |             val oldLearningLanguage = languageSettingsLocalDataSource.learningLanguage.first()
🔴   30 |             languageSettingsLocalDataSource.setTranslationLanguage(oldLearningLanguage)
⚪   31 |         }
🟢   32 |         languageSettingsLocalDataSource.setLearningLanguage(language)
```

## Lines 38-45

Location: `src/main/java/input/comprehensible/data/languages/LanguageSettingsRepository.kt:38-45`

```kotlin
⚪   38 |     suspend fun setTranslationLanguage(language: String) {
🟢   39 |         val learningLanguage = learningLanguage.first()
🟡   40 |         if (language == learningLanguage) {
🔴   41 |             val oldTranslationLanguage =
🔴   42 |                 languageSettingsLocalDataSource.translationsLanguage.first()
🔴   43 |             languageSettingsLocalDataSource.setLearningLanguage(oldTranslationLanguage)
⚪   44 |         }
🟢   45 |         languageSettingsLocalDataSource.setTranslationLanguage(language)
```
