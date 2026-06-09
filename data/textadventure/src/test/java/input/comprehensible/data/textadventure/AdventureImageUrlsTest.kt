package input.comprehensible.data.textadventure

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AdventureImageUrlsTest {

    @Test
    fun `forId builds the light asset url for a catalogue image id`() {
        // GIVEN a backend base url and a catalogue image id
        val baseUrl = "https://api.test"
        val imageId = "forest-path"

        // WHEN the light cover-image url is formed
        val url = AdventureImageUrls.forId(baseUrl, imageId)

        // THEN it points at the served light asset for that id
        assertEquals("https://api.test/adventure-images/forest-path.webp", url)
    }

    @Test
    fun `forId is null when the image id is null`() {
        // GIVEN a backend base url and no image id
        val baseUrl = "https://api.test"

        // WHEN the light cover-image url is formed
        val url = AdventureImageUrls.forId(baseUrl, null)

        // THEN there is no cover-image url
        assertNull(url)
    }

    @Test
    fun `forId is null when the image id is blank`() {
        // GIVEN a backend base url and a blank image id
        val baseUrl = "https://api.test"
        val blankImageId = "   "

        // WHEN the light cover-image url is formed
        val url = AdventureImageUrls.forId(baseUrl, blankImageId)

        // THEN there is no cover-image url
        assertNull(url)
    }

    @Test
    fun `darkVariant inserts the dark suffix before the extension`() {
        // GIVEN the light url of a cover image
        val lightUrl = "https://api.test/adventure-images/forest-path.webp"

        // WHEN its dark-theme variant is formed
        val darkUrl = AdventureImageUrls.darkVariant(lightUrl)

        // THEN the dark suffix is inserted before the file extension
        assertEquals("https://api.test/adventure-images/forest-path-dark.webp", darkUrl)
    }
}
