# src/main/java/input/comprehensible/usecases/ContinueTextAdventureUseCase.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 7-14

Location: `src/main/java/input/comprehensible/usecases/ContinueTextAdventureUseCase.kt:7-14`

```kotlin
⚪    7 |  */
⚪    8 | class ContinueTextAdventureUseCase(
🔴    9 |     private val repository: TextAdventuresRepository = TextAdventuresRepository(),
⚪   10 | ) {
⚪   11 |     suspend operator fun invoke(adventureId: String, userMessage: String) {
🔴   12 |         repository.respondToAdventure(adventureId = adventureId, userMessage = userMessage)
⚪   13 |     }
⚪   14 | }
```
