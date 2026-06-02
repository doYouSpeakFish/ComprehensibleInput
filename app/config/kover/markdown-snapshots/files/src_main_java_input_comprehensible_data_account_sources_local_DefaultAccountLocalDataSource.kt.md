# src/main/java/input/comprehensible/data/account/sources/local/DefaultAccountLocalDataSource.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 28-32

Location: `src/main/java/input/comprehensible/data/account/sources/local/DefaultAccountLocalDataSource.kt:28-32`

```kotlin
🟢   28 |         Session(
🟢   29 |             email = it[EMAIL] ?: return@map null,
🟡   30 |             token = it[SESSION_TOKEN] ?: return@map null,
🟢   31 |         )
⚪   32 |     }
```
