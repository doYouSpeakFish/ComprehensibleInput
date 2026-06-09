package input.comprehensible.data.textadventure

/**
 * How the app forms URLs for the backend's static adventure cover images. Centralised so the light
 * URL and its dark-theme variant stay in sync with the backend's adventure-image assets: a catalogue
 * image id resolves to the light asset `"$baseUrl/adventure-images/<id>.webp"`, and the backend serves
 * a dark-theme variant `"<id>-dark.webp"` alongside it that the app uses when displaying in dark theme.
 */
object AdventureImageUrls {
    private const val PATH = "adventure-images"
    private const val EXTENSION = "webp"
    private const val DARK_SUFFIX = "-dark"

    /**
     * The light-theme cover image URL for the catalogue [imageId] under [baseUrl], or null when the
     * adventure has no image (a null or blank id).
     */
    fun forId(baseUrl: String, imageId: String?): String? =
        imageId?.takeIf { it.isNotBlank() }?.let { "$baseUrl/$PATH/$it.$EXTENSION" }

    /**
     * The dark-theme variant of a light cover image [url]: the `"<id>-dark.webp"` asset the backend
     * serves alongside the `"<id>.webp"` one, formed by inserting [DARK_SUFFIX] before the extension.
     */
    fun darkVariant(url: String): String = url.removeSuffix(".$EXTENSION") + "$DARK_SUFFIX.$EXTENSION"
}
