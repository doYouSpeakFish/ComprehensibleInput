# src/main/java/input/comprehensible/data/textadventures/model/TextAdventure.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 4-14

Location: `src/main/java/input/comprehensible/data/textadventures/model/TextAdventure.kt:4-14`

```kotlin
⚪    4 |  * Represents a text adventure and its messages.
⚪    5 |  */
🔴    6 | data class TextAdventure(
🔴    7 |     val id: String,
🔴    8 |     val title: String,
🔴    9 |     val learningLanguage: String,
🔴   10 |     val translationLanguage: String,
🔴   11 |     val messages: List<TextAdventureMessage>,
🔴   12 |     val isComplete: Boolean,
⚪   13 | )
⚪   14 | 
```

## Lines 16-24

Location: `src/main/java/input/comprehensible/data/textadventures/model/TextAdventure.kt:16-24`

```kotlin
⚪   16 |  * A single message in a text adventure.
⚪   17 |  */
🔴   18 | data class TextAdventureMessage(
🔴   19 |     val id: String,
🔴   20 |     val sender: TextAdventureMessageSender,
🔴   21 |     val paragraphs: List<TextAdventureParagraph>,
🔴   22 |     val isEnding: Boolean,
⚪   23 | )
⚪   24 | 
```

## Lines 26-33

Location: `src/main/java/input/comprehensible/data/textadventures/model/TextAdventure.kt:26-33`

```kotlin
⚪   26 |  * A paragraph inside a text adventure message.
⚪   27 |  */
🔴   28 | data class TextAdventureParagraph(
🔴   29 |     val id: String,
🔴   30 |     val sentences: List<String>,
🔴   31 |     val translatedSentences: List<String>,
⚪   32 | )
⚪   33 | 
```

## Lines 36-41

Location: `src/main/java/input/comprehensible/data/textadventures/model/TextAdventure.kt:36-41`

```kotlin
⚪   36 |  */
⚪   37 | enum class TextAdventureMessageSender {
🔴   38 |     AI,
🔴   39 |     USER,
⚪   40 | }
⚪   41 | 
```

## Lines 43-50

Location: `src/main/java/input/comprehensible/data/textadventures/model/TextAdventure.kt:43-50`

```kotlin
⚪   43 |  * A list item for a text adventure.
⚪   44 |  */
🔴   45 | data class TextAdventureSummary(
🔴   46 |     val id: String,
🔴   47 |     val title: String,
🔴   48 |     val isComplete: Boolean,
🔴   49 |     val updatedAt: Long,
⚪   50 | )
```
