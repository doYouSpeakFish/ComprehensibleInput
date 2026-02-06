# app/src/main/java/input/comprehensible/App.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported


## Lines 14-19

Location: `app/src/main/java/input/comprehensible/App.kt:14-19`

```kotlin
⚪   14 | 
⚪   15 | fun Application.injectDependencies() {
🔴   16 |     ApplicationProvider.inject { this }
🔴   17 |     CoroutinesModule.inject()
🔴   18 |     DataSourcesModule.inject()
⚪   19 | }
```

