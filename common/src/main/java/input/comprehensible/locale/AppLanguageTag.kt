package input.comprehensible.locale

import android.content.Context
import input.comprehensible.common.R
import input.comprehensible.di.ApplicationProvider

/**
 * Returns the BCP-47 language tag of the locale the app's strings are currently displayed in (for
 * example "en", "de" or "id").
 *
 * Android resolves this from the user's device/app locale against the translations the app actually
 * ships, so it always matches what the user sees on screen — unlike the raw device locale, which can
 * name a language the app has no translations for. It is intentionally separate from the learning
 * and translation language settings, which select story and adventure content rather than the app's
 * own UI language.
 *
 * The value comes from the per-locale [R.string.app_language_tag] resource, so it tracks Android's
 * resource resolution exactly: whichever `values-*` variant is selected supplies its own tag.
 */
fun appLanguageTag(context: Context = ApplicationProvider()): String =
    context.getString(R.string.app_language_tag)
