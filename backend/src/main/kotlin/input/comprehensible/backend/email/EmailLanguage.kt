package input.comprehensible.backend.email

/**
 * The set of UI languages the app ships translations for, and therefore the set of languages the
 * backend can localise account emails into.
 *
 * The app reports the language it is currently displaying to the user via the request's
 * `Accept-Language` header (see [fromAcceptLanguageHeader]). This is deliberately the app's own UI
 * language and is independent of the learning and translation languages the user chooses for story
 * and adventure content.
 *
 * [ENGLISH] is the default and the fallback for any language the app (and therefore the backend)
 * does not support. Keep this list in sync with the app's `values-*` resource folders.
 */
enum class EmailLanguage(val tag: String) {
    ENGLISH("en"),
    GERMAN("de"),
    SPANISH("es"),
    FRENCH("fr"),
    PORTUGUESE("pt"),
    INDONESIAN("id"),
    ;

    companion object {
        /**
         * Resolves the best supported language from an HTTP `Accept-Language` header value.
         *
         * Quality weights are honoured so the client's most preferred supported language wins, region
         * subtags are ignored (so `pt-BR` matches [PORTUGUESE]), and the obsolete Indonesian code
         * `in` is treated as `id`. Falls back to [ENGLISH] when the header is missing, malformed, or
         * lists only unsupported languages.
         */
        fun fromAcceptLanguageHeader(header: String?): EmailLanguage {
            if (header.isNullOrBlank()) return ENGLISH
            return header.split(',')
                .mapNotNull(::parseRangeOrNull)
                .sortedByDescending { it.quality }
                .firstNotNullOfOrNull { fromLanguageSubtag(it.subtag) }
                ?: ENGLISH
        }

        private fun fromLanguageSubtag(subtag: String): EmailLanguage? = when (subtag) {
            // "in" is the obsolete ISO 639 code for Indonesian that older JVM/Android locales emit.
            "in" -> INDONESIAN
            else -> entries.firstOrNull { it.tag == subtag }
        }

        private fun parseRangeOrNull(range: String): LanguageRange? {
            val segments = range.split(';')
            val subtag = segments.first().trim().substringBefore('-').lowercase()
            if (subtag.isEmpty() || subtag == "*") return null
            val quality = segments
                .firstOrNull { it.trim().startsWith("q=") }
                ?.let { it.substringAfter('=').trim().toDoubleOrNull() }
                ?: DEFAULT_QUALITY
            // q=0 means the client explicitly rejects this language (RFC 7231), so drop it.
            if (quality <= NOT_ACCEPTABLE_QUALITY) return null
            return LanguageRange(subtag, quality)
        }

        private const val DEFAULT_QUALITY = 1.0
        private const val NOT_ACCEPTABLE_QUALITY = 0.0
    }
}

private data class LanguageRange(val subtag: String, val quality: Double)
