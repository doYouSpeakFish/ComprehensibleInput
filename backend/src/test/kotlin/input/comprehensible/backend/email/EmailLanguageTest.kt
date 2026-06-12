package input.comprehensible.backend.email

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EmailLanguageTest {

    @Test
    fun `a null header falls back to English`() {
        // GIVEN no Accept-Language header
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader(null)

        // THEN it falls back to English
        assertEquals(EmailLanguage.ENGLISH, language)
    }

    @Test
    fun `a blank header falls back to English`() {
        // GIVEN a blank Accept-Language header
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("   ")

        // THEN it falls back to English
        assertEquals(EmailLanguage.ENGLISH, language)
    }

    @Test
    fun `each supported tag resolves to its own language`() {
        EmailLanguage.entries.forEach { language ->
            // GIVEN a supported language's own tag
            // WHEN the email language is resolved from that tag
            val resolved = EmailLanguage.fromAcceptLanguageHeader(language.tag)

            // THEN it resolves back to the same language
            assertEquals(language, resolved)
        }
    }

    @Test
    fun `region subtags are ignored`() {
        // GIVEN headers that carry a region subtag
        // WHEN the email language is resolved
        // THEN only the language subtag is matched
        assertEquals(EmailLanguage.PORTUGUESE, EmailLanguage.fromAcceptLanguageHeader("pt-BR"))
        assertEquals(EmailLanguage.FRENCH, EmailLanguage.fromAcceptLanguageHeader("fr-FR"))
    }

    @Test
    fun `the legacy Indonesian code maps to Indonesian`() {
        // GIVEN the obsolete Indonesian language code "in"
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("in")

        // THEN it maps to Indonesian
        assertEquals(EmailLanguage.INDONESIAN, language)
    }

    @Test
    fun `an unsupported language falls back to English`() {
        // GIVEN a header naming a language the backend does not support
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("ja")

        // THEN it falls back to English
        assertEquals(EmailLanguage.ENGLISH, language)
    }

    @Test
    fun `quality weights pick the most preferred supported language`() {
        // GIVEN two supported languages with different quality weights
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("de;q=0.5, es;q=0.9")

        // THEN the highest-weighted one wins
        assertEquals(EmailLanguage.SPANISH, language)
    }

    @Test
    fun `unsupported languages are skipped in favour of supported ones`() {
        // GIVEN a header that lists an unsupported language ahead of supported ones
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("ja, de, es")

        // THEN the first supported language is used
        assertEquals(EmailLanguage.GERMAN, language)
    }

    @Test
    fun `an implicit quality outranks a lower explicit one`() {
        // GIVEN a language with an implicit weight of 1.0 ahead of a lower explicit weight
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("fr, es;q=0.8")

        // THEN the implicitly-weighted language wins
        assertEquals(EmailLanguage.FRENCH, language)
    }

    @Test
    fun `a wildcard falls back to English`() {
        // GIVEN a wildcard Accept-Language header
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("*")

        // THEN it falls back to English
        assertEquals(EmailLanguage.ENGLISH, language)
    }

    @Test
    fun `surrounding whitespace and case are tolerated`() {
        // GIVEN a header with surrounding whitespace and upper-case letters
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("  DE  ")

        // THEN it still matches the language
        assertEquals(EmailLanguage.GERMAN, language)
    }

    @Test
    fun `a malformed quality is treated as the default weight`() {
        // GIVEN a language whose quality weight cannot be parsed, ahead of a lower explicit weight
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("es;q=abc, de;q=0.5")

        // THEN the malformed weight defaults to 1.0 and that language wins
        assertEquals(EmailLanguage.SPANISH, language)
    }

    @Test
    fun `empty ranges are skipped`() {
        // GIVEN a header with a trailing empty range
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("de,")

        // THEN the empty range is ignored and the real language is used
        assertEquals(EmailLanguage.GERMAN, language)
    }

    @Test
    fun `a language explicitly rejected with zero quality is not used`() {
        // GIVEN a header that rejects its only supported language with q=0
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("de;q=0")

        // THEN the rejected language is not used and it falls back to English
        assertEquals(EmailLanguage.ENGLISH, language)
    }

    @Test
    fun `a rejected language is skipped in favour of an acceptable one`() {
        // GIVEN a header that rejects one supported language but accepts another
        // WHEN the email language is resolved
        val language = EmailLanguage.fromAcceptLanguageHeader("de;q=0, es")

        // THEN the rejected language is skipped and the acceptable one is used
        assertEquals(EmailLanguage.SPANISH, language)
    }
}
