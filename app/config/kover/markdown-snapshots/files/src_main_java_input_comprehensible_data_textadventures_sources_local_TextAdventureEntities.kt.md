# src/main/java/input/comprehensible/data/textadventures/sources/local/TextAdventureEntities.kt

**Key**

- 🟢 Covered
- 🔴 Missed
- 🟡 Partially covered (missing branches or instructions)
- ⚪ Excluded or not reported

## Lines 8-22

Location: `src/main/java/input/comprehensible/data/textadventures/sources/local/TextAdventureEntities.kt:8-22`

```kotlin
⚪    8 | import input.comprehensible.data.textadventures.model.TextAdventureMessageSender
⚪    9 | 
🔴   10 | @Entity
⚪   11 | data class TextAdventureEntity(
🔴   12 |     @PrimaryKey val id: String,
🔴   13 |     val title: String,
🔴   14 |     val learningLanguage: String,
🔴   15 |     val translationLanguage: String,
🔴   16 |     val createdAt: Long,
🔴   17 |     val updatedAt: Long,
⚪   18 | )
⚪   19 | 
🔴   20 | @Entity(
⚪   21 |     foreignKeys = [
⚪   22 |         ForeignKey(
```

## Lines 31-42

Location: `src/main/java/input/comprehensible/data/textadventures/sources/local/TextAdventureEntities.kt:31-42`

```kotlin
⚪   31 | )
⚪   32 | data class TextAdventureMessageEntity(
🔴   33 |     val adventureId: String,
🔴   34 |     val sender: TextAdventureMessageSender,
🔴   35 |     val isEnding: Boolean,
🔴   36 |     val createdAt: Long,
🔴   37 |     val messageIndex: Int,
⚪   38 | )
⚪   39 | 
🔴   40 | @Entity(
⚪   41 |     foreignKeys = [
⚪   42 |         ForeignKey(
```

## Lines 51-63

Location: `src/main/java/input/comprehensible/data/textadventures/sources/local/TextAdventureEntities.kt:51-63`

```kotlin
⚪   51 | )
⚪   52 | data class TextAdventureSentenceEntity(
🔴   53 |     val adventureId: String,
🔴   54 |     val messageIndex: Int,
🔴   55 |     val paragraphIndex: Int,
🔴   56 |     val sentenceIndex: Int,
🔴   57 |     val language: String,
🔴   58 |     val text: String,
⚪   59 | )
⚪   60 | 
🔴   61 | @DatabaseView(
⚪   62 |     """
⚪   63 |     SELECT
```

## Lines 79-91

Location: `src/main/java/input/comprehensible/data/textadventures/sources/local/TextAdventureEntities.kt:79-91`

```kotlin
⚪   79 | )
⚪   80 | data class TextAdventureSummaryView(
🔴   81 |     val adventureId: String,
🔴   82 |     val title: String,
🔴   83 |     val learningLanguage: String,
🔴   84 |     val translationLanguage: String,
🔴   85 |     val updatedAt: Long,
🔴   86 |     val isComplete: Boolean,
⚪   87 | )
⚪   88 | 
🔴   89 | @DatabaseView(
⚪   90 |     """
⚪   91 |     SELECT
```

## Lines 110-123

Location: `src/main/java/input/comprehensible/data/textadventures/sources/local/TextAdventureEntities.kt:110-123`

```kotlin
⚪  110 | )
⚪  111 | data class TextAdventureMessageSentenceView(
🔴  112 |     val adventureId: String,
🔴  113 |     val title: String,
🔴  114 |     val learningLanguage: String,
🔴  115 |     val translationLanguage: String,
🔴  116 |     val messageIndex: Int,
🔴  117 |     val sender: TextAdventureMessageSender,
🔴  118 |     val isEnding: Boolean,
🔴  119 |     val paragraphIndex: Int,
🔴  120 |     val sentenceIndex: Int,
🔴  121 |     val language: String,
🔴  122 |     val text: String,
⚪  123 | )
```
