# src/main/kotlin/input/comprehensible/backend/common/EnvironmentSecrets.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 4-25

Location: `src/main/kotlin/input/comprehensible/backend/common/EnvironmentSecrets.kt:4-25`

```kotlin
⚪    4 | 
⚪    5 | fun requireSecretValue(envVarName: String): String {
🔴    6 |     val directValue = System.getenv(envVarName)?.takeIf { it.isNotBlank() }
🔴    7 |     if (directValue != null) {
🔴    8 |         return directValue
⚪    9 |     }
⚪   10 | 
🔴   11 |     val fileEnvVarName = "${envVarName}_FILE"
🔴   12 |     val secretFilePath = System.getenv(fileEnvVarName)?.takeIf { it.isNotBlank() }
🔴   13 |     if (secretFilePath != null) {
🔴   14 |         val secretValue = File(secretFilePath).readText().trim()
🔴   15 |         require(secretValue.isNotEmpty()) {
🔴   16 |             "Environment variable $fileEnvVarName points to an empty file: $secretFilePath"
⚪   17 |         }
🔴   18 |         return secretValue
⚪   19 |     }
⚪   20 | 
🔴   21 |     error(
🔴   22 |         "Missing required environment variable $envVarName. " +
🔴   23 |             "Set $envVarName directly or set ${envVarName}_FILE to a file containing the value."
⚪   24 |     )
⚪   25 | }
```
