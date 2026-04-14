# src/main/kotlin/input/comprehensible/backend/Application.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 12-21

Location: `src/main/kotlin/input/comprehensible/backend/Application.kt:12-21`

```kotlin
⚪   12 | 
⚪   13 | fun main() {
🔴   14 |     embeddedServer(
🔴   15 |         factory = Netty,
🔴   16 |         port = 8080,
🔴   17 |         host = "0.0.0.0",
🔴   18 |         module = Application::configureRouting,
🔴   19 |     ).start(wait = true)
⚪   20 | }
⚪   21 | 
```
