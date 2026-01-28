package input.comprehensible.data.textadventure

import input.comprehensible.data.textadventure.sources.remote.TextAdventureRemoteResponse
import java.util.ArrayDeque

class TextAdventureTestData {
    private val responses = ArrayDeque<TextAdventureRemoteResponse>()

    var scenario: TextAdventureRemoteResponse = TextAdventureRemoteResponse(
        adventureId = "text-adventure",
        sentences = listOf("You arrive at the start of a new adventure."),
        translatedSentences = listOf("Llegas al comienzo de una nueva aventura."),
        isEnding = false,
    )
        private set

    fun setScenario(response: TextAdventureRemoteResponse) {
        scenario = response
    }

    fun setResponses(responses: List<TextAdventureRemoteResponse>) {
        this.responses.clear()
        this.responses.addAll(responses)
    }

    fun nextResponse(): TextAdventureRemoteResponse {
        check(responses.isNotEmpty()) { "No text adventure responses configured." }
        return responses.removeFirst()
    }
}
