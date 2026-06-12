package input.comprehensible.backend.email

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EmailLanguageTest {

    @Test
    fun `a null header falls back to English`() {
        assertEquals(EmailLanguage.ENGLISH, EmailLanguage.fromAcceptLanguageHeader(null))
    }

    @Test
    fun `a blank header falls back to English`() {
        assertEquals(EmailLanguage.ENGLISH, EmailLanguage.fromAcceptLanguageHeader("   "))
    }

    @Test
    fun `each supported tag resolves to its own language`() {
        EmailLanguage.entries.forEach { language ->
            assertEquals(language, EmailLanguage.fromAcceptLanguageHeader(language.tag))
        }
    }

    @Test
    fun `region subtags are ignored`() {
        assertEquals(EmailLanguage.PORTUGUESE, EmailLanguage.fromAcceptLanguageHeader("pt-BR"))
        assertEquals(EmailLanguage.FRENCH, EmailLanguage.fromAcceptLanguageHeader("fr-FR"))
    }

    @Test
    fun `the legacy Indonesian code maps to Indonesian`() {
        assertEquals(EmailLanguage.INDONESIAN, EmailLanguage.fromAcceptLanguageHeader("in"))
    }

    @Test
    fun `an unsupported language falls back to English`() {
        assertEquals(EmailLanguage.ENGLISH, EmailLanguage.fromAcceptLanguageHeader("ja"))
    }

    @Test
    fun `quality weights pick the most preferred supported language`() {
        assertEquals(EmailLanguage.SPANISH, EmailLanguage.fromAcceptLanguageHeader("de;q=0.5, es;q=0.9"))
    }

    @Test
    fun `unsupported languages are skipped in favour of supported ones`() {
        assertEquals(EmailLanguage.GERMAN, EmailLanguage.fromAcceptLanguageHeader("ja, de, es"))
    }

    @Test
    fun `an implicit quality outranks a lower explicit one`() {
        assertEquals(EmailLanguage.FRENCH, EmailLanguage.fromAcceptLanguageHeader("fr, es;q=0.8"))
    }

    @Test
    fun `a wildcard falls back to English`() {
        assertEquals(EmailLanguage.ENGLISH, EmailLanguage.fromAcceptLanguageHeader("*"))
    }

    @Test
    fun `a malformed quality is treated as the default weight`() {
        // "es" has an unparseable weight so it defaults to 1.0, outranking the explicit 0.5 on "de".
        assertEquals(EmailLanguage.SPANISH, EmailLanguage.fromAcceptLanguageHeader("es;q=abc, de;q=0.5"))
    }

    @Test
    fun `empty ranges are skipped`() {
        assertEquals(EmailLanguage.GERMAN, EmailLanguage.fromAcceptLanguageHeader("de,"))
    }

    @Test
    fun `surrounding whitespace and case are tolerated`() {
        assertEquals(EmailLanguage.GERMAN, EmailLanguage.fromAcceptLanguageHeader("  DE  "))
    }
}
