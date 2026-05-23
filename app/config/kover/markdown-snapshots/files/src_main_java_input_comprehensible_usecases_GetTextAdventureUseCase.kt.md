# src/main/java/input/comprehensible/usecases/GetTextAdventureUseCase.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 9-14

Location: `src/main/java/input/comprehensible/usecases/GetTextAdventureUseCase.kt:9-14`

```kotlin
⚪    9 |  */
⚪   10 | class GetTextAdventureUseCase(
🔴   11 |     private val repository: TextAdventuresRepository = TextAdventuresRepository(),
⚪   12 | ) {
🔴   13 |     operator fun invoke(id: String): Flow<TextAdventureResult> = repository.getAdventure(id)
⚪   14 | }
```
