package input.comprehensible.features.textadventure

import input.comprehensible.data.textadventures.sources.remote.TextAdventureRemoteResponse

/** A scripted text adventure: the opening [scenario] and the AI [responses] returned in order. */
internal data class ScriptedAdventure(
    val scenario: TextAdventureRemoteResponse,
    val responses: List<TextAdventureRemoteResponse>,
)

/** Named text adventure fixtures used by the Cucumber scenarios. */
internal object TextAdventureFixtures {
    fun byName(name: String): ScriptedAdventure = when (name) {
        "Harbor Mist" -> harborMist
        "Forest Echoes" -> forestEchoes
        else -> error("Unknown text adventure fixture: $name")
    }

    private val harborMist = ScriptedAdventure(
        scenario = TextAdventureRemoteResponse(
            adventureId = "adventure-1",
            title = "Harbor Mist",
            sentences = listOf("You arrive at a quiet harbor."),
            translatedSentences = listOf("Llegas a un puerto tranquilo."),
            isEnding = false,
        ),
        responses = listOf(
            TextAdventureRemoteResponse(
                adventureId = "adventure-1",
                title = "Harbor Mist",
                sentences = listOf("A lantern flickers on the dock."),
                translatedSentences = listOf("Una linterna parpadea en el muelle."),
                isEnding = false,
            ),
            TextAdventureRemoteResponse(
                adventureId = "adventure-1",
                title = "Harbor Mist",
                sentences = listOf("The fog lifts and the journey ends."),
                translatedSentences = listOf("La niebla se disipa y el viaje termina."),
                isEnding = true,
            ),
        ),
    )

    private val forestEchoes = ScriptedAdventure(
        scenario = TextAdventureRemoteResponse(
            adventureId = "adventure-2",
            title = "Forest Echoes",
            sentences = listOf("A trail winds into the forest."),
            translatedSentences = listOf("Un sendero se adentra en el bosque."),
            isEnding = false,
        ),
        responses = listOf(
            TextAdventureRemoteResponse(
                adventureId = "adventure-2",
                title = "Forest Echoes",
                sentences = listOf("Birdsong follows you between the trees."),
                translatedSentences = listOf("El canto de los pájaros te sigue entre los árboles."),
                isEnding = false,
            ),
        ),
    )
}
