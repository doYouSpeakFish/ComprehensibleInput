package input.comprehensible.backend.textadventure

/**
 * A single adventure cover image the AI can choose from when starting a new adventure.
 *
 * [id] doubles as the static asset's file name stem: the image
 * is served at `/$ADVENTURE_IMAGES_PATH/$id.$ADVENTURE_IMAGE_EXTENSION`, with a dark-theme variant
 * served alongside it at `/$ADVENTURE_IMAGES_PATH/$id-dark.$ADVENTURE_IMAGE_EXTENSION` for the app to
 * use in dark theme.
 *
 * [name] is a short, human-readable label for the image (for example "Forest Path"). It identifies
 * the image in a way people can read and say, rather than by its [id] file stem.
 *
 * [description] is a real, human-written description of what the image depicts. It is shown to the
 * language model so it can pick the image that best fits the opening scene, and it is also the brief
 * used to generate the real artwork that will replace the placeholder.
 */
data class AdventureImage(
    val id: String,
    val name: String,
    val description: String,
)

/**
 * The fixed catalogue of adventure cover images bundled with the backend.
 *
 * The descriptions are deliberately varied (different places, moods and times of day) so the AI can
 * find a fitting image for a wide range of adventures, and so adventures look distinct from one
 * another in the list.
 */
object AdventureImageCatalog {

    val images: List<AdventureImage> = listOf(
        AdventureImage(
            id = "forest-path",
            name = "Forest Path",
            description = "A sun-dappled dirt path winding deep into an ancient forest, " +
                "shafts of golden light breaking through a dense green canopy.",
        ),
        AdventureImage(
            id = "mountain-peak",
            name = "Mountain Peak",
            description = "A lone snow-capped mountain peak rising above a sea of clouds at dawn, " +
                "soft pink and orange light glowing on the snow.",
        ),
        AdventureImage(
            id = "coastal-village",
            name = "Coastal Village",
            description = "A small fishing village of whitewashed houses clustered around a calm " +
                "harbour, wooden boats moored along a worn stone quay.",
        ),
        AdventureImage(
            id = "desert-dunes",
            name = "Desert Dunes",
            description = "Endless rolling golden sand dunes under a vast clear blue sky, a single " +
                "line of footprints trailing off toward the horizon.",
        ),
        AdventureImage(
            id = "castle-gate",
            name = "Castle Gate",
            description = "The towering stone gate of a medieval castle at dusk, torches flickering " +
                "on either side of a heavy iron-bound wooden door.",
        ),
        AdventureImage(
            id = "city-market",
            name = "City Market",
            description = "A bustling open-air market in an old city, colourful stalls of fruit and " +
                "fabric crowded beneath striped awnings.",
        ),
        AdventureImage(
            id = "starry-night",
            name = "Starry Night",
            description = "A wide night sky brilliant with stars and the glowing band of the Milky " +
                "Way arching over a dark, silhouetted hilltop.",
        ),
        AdventureImage(
            id = "river-crossing",
            name = "River Crossing",
            description = "A wooden rope bridge spanning a wide, fast-flowing river in a green " +
                "valley, mist rising gently from the water below.",
        ),
        AdventureImage(
            id = "snowy-cabin",
            name = "Snowy Cabin",
            description = "A cosy log cabin half-buried in fresh snow, warm yellow light glowing " +
                "from its windows beneath tall pine trees.",
        ),
        AdventureImage(
            id = "ancient-ruins",
            name = "Ancient Ruins",
            description = "Crumbling stone columns of a forgotten temple overgrown with vines, " +
                "half-swallowed by dense jungle.",
        ),
        AdventureImage(
            id = "lighthouse-cliff",
            name = "Lighthouse Cliff",
            description = "A red-and-white lighthouse standing on a rugged sea cliff, waves crashing " +
                "on the rocks far below under a brooding stormy sky.",
        ),
        AdventureImage(
            id = "underground-cavern",
            name = "Underground Cavern",
            description = "A vast underground cavern lit by softly glowing blue crystals, a still " +
                "subterranean lake mirroring the light.",
        ),
        AdventureImage(
            id = "autumn-park",
            name = "Autumn Park",
            description = "A quiet city park in autumn, a path carpeted with red and orange leaves " +
                "beside a calm reflecting pond.",
        ),
        AdventureImage(
            id = "space-station",
            name = "Space Station",
            description = "The sleek interior corridor of a space station, large windows looking out " +
                "on a blue planet turning slowly below.",
        ),
        AdventureImage(
            id = "tropical-beach",
            name = "Tropical Beach",
            description = "A deserted tropical beach with turquoise water and white sand, palm trees " +
                "leaning over the shore in bright sunlight.",
        ),
        AdventureImage(
            id = "train-station",
            name = "Train Station",
            description = "A grand old railway station hall with an arched glass roof, travellers " +
                "hurrying beneath a large iron clock.",
        ),
        AdventureImage(
            id = "enchanted-garden",
            name = "Enchanted Garden",
            description = "A lush walled garden full of blossoming flowers, a small stone fountain " +
                "at its centre and butterflies drifting in the air.",
        ),
        AdventureImage(
            id = "stormy-sea",
            name = "Stormy Sea",
            description = "A wooden sailing ship riding tall waves on a dark, stormy sea, its sails " +
                "straining against a fierce wind.",
        ),
        AdventureImage(
            id = "alpine-village-night",
            name = "Alpine Village at Night",
            description = "A small alpine village at night, warm lights in the windows of timber " +
                "houses, snow on the rooftops and stars overhead.",
        ),
        AdventureImage(
            id = "library-hall",
            name = "Library Hall",
            description = "A grand old library hall with towering shelves of books, tall windows and " +
                "a spiral staircase, dust glinting in slanting sunbeams.",
        ),
    )

    private val imagesById: Map<String, AdventureImage> = images.associateBy { it.id }
    private val imagesByName: Map<String, AdventureImage> = images.associateBy { it.name }

    /** The image used when the AI does not pick a valid one; kept deterministic for predictability. */
    val fallback: AdventureImage = images.first()

    fun findById(id: String?): AdventureImage? = id?.let { imagesById[it] }

    /** Finds an image by its human-readable [AdventureImage.name], or null when there is no match. */
    fun findByName(name: String): AdventureImage? = imagesByName[name]

    fun contains(id: String?): Boolean = findById(id) != null

    /**
     * The catalogue rendered as a numbered `id: description` list for inclusion in a prompt, so the
     * model can read every option and reply with one of the [AdventureImage.id] values.
     */
    fun promptListing(): String = images.joinToString(separator = "\n") { "- ${it.id}: ${it.description}" }

    init {
        require(images.size == EXPECTED_IMAGE_COUNT) {
            "Adventure image catalogue must contain $EXPECTED_IMAGE_COUNT images but had ${images.size}"
        }
        require(imagesById.size == images.size) { "Adventure image ids must be unique" }
        require(imagesByName.size == images.size) { "Adventure image names must be unique" }
    }

    private const val EXPECTED_IMAGE_COUNT = 20
}

/** The static-content sub-path under which adventure images are served. */
const val ADVENTURE_IMAGES_PATH: String = "adventure-images"

/** The file extension (and Coil-decodable format) of every bundled adventure image. */
const val ADVENTURE_IMAGE_EXTENSION: String = "webp"
