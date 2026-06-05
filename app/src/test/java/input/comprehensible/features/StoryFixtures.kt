package input.comprehensible.features

import input.comprehensible.data.sample.SampleStoriesData
import input.comprehensible.data.sample.TestStory
import input.comprehensible.data.sample.filterParagraphs

/**
 * Resolves the ordinals ("first", "second", "last") and language names used in the app feature
 * files to the deterministic sample data, so the step definitions stay free of raw test strings.
 */
internal object StoryFixtures {
    val stories: List<TestStory> = SampleStoriesData.listOf100Stories
    val chooseYourOwnAdventureStory: TestStory = SampleStoriesData.chooseYourOwnAdventureStory

    fun story(ordinal: String): TestStory = when (ordinal) {
        "first" -> stories.first()
        "second" -> stories[1]
        "last" -> stories.last()
        else -> error("Unknown story ordinal: $ordinal")
    }

    fun languageCode(name: String): String = when (name) {
        "German" -> "de"
        "English" -> "en"
        "Spanish" -> "es"
        else -> error("Unknown language: $name")
    }

    fun title(story: TestStory, language: String): String = when (language) {
        "de" -> story.germanTitle
        "en" -> story.englishTitle
        "es" -> story.spanishTitle
        else -> error("Unknown language code: $language")
    }

    fun firstParagraphSentences(story: TestStory, language: String): List<String> {
        val paragraph = story.paragraphs.first()
        return when (language) {
            "de" -> paragraph.germanSentences
            "en" -> paragraph.englishSentences
            "es" -> paragraph.spanishSentences
            else -> error("Unknown language code: $language")
        }
    }

    /** A sentence partway through a [story], used to verify the saved reading position. */
    fun savedPositionSentence(story: TestStory): String = story.paragraphs[3].germanSentences[9]

    /** The branching story's choice label for [choice] ("keep the key" / "return the key"). */
    fun adventureChoiceText(choice: String, language: String): String =
        chooseYourOwnAdventureStory.getPart(adventurePartId(choice)).choice!!.textByLanguage.getValue(language)

    /** The first German sentence of a branching story part ("start" / "keep the key" / "return the key"). */
    fun adventurePartGermanSentence(part: String): String =
        chooseYourOwnAdventureStory.getPart(adventurePartId(part)).content.filterParagraphs().first().germanSentences.first()

    private fun adventurePartId(name: String): String = when (name) {
        "start" -> "start"
        "keep the key" -> "keep_key"
        "return the key" -> "return_key"
        else -> error("Unknown adventure part: $name")
    }
}
