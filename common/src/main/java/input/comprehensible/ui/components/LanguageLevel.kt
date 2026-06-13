package input.comprehensible.ui.components

import androidx.annotation.StringRes
import input.comprehensible.common.R

/**
 * The CEFR difficulty levels a learner can pick for the content they are given. The [code] (e.g.
 * "B1") is shown verbatim in every language and is also the value stored and sent to the backend;
 * [description] is a plain-language gloss (e.g. "Intermediate") shown alongside the code in the
 * picker for learners unfamiliar with the CEFR scale.
 */
enum class LanguageLevel(
    val code: String,
    @param:StringRes val description: Int,
) {
    A1("A1", R.string.language_level_a1_description),
    A2("A2", R.string.language_level_a2_description),
    B1("B1", R.string.language_level_b1_description),
    B2("B2", R.string.language_level_b2_description),
    C1("C1", R.string.language_level_c1_description),
    ;

    companion object {
        /** The level the app starts new learners at, and the backend's backwards-compatible default. */
        val DEFAULT: LanguageLevel = B1

        /** Resolves a stored or wire [code] (e.g. "B1") to a level, falling back to [DEFAULT]. */
        fun fromCode(code: String?): LanguageLevel =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DEFAULT
    }
}
