package input.comprehensible.storygen

import kotlin.test.Test

class AdventureGenerationTests {
    @Test
    fun `a traveler experiences branching paths that all end well`() = adventureScenario {
        // GIVEN the guide is ready with a lively path and two endings
        givenTheJourneyCanReachAtMost(depth = 2)
        givenTheGuideWillShare(
            chapter(
                title = "At the crossroads",
                narrative = "The traveler stands at a moonlit fork in the woods.",
                turn("Take the winding trail", "A cautious climb"),
                turn("Follow the glowing river", "A shimmering glide"),
            ),
            chapter(
                title = "The high ridge",
                narrative = "The climb reveals a breathtaking view of distant lanterns.",
                turn("Signal the distant lights", "Call for allies"),
                turn("Descend to the valley", "Seek the hidden market"),
            ),
            finale(
                title = "A celebration in the market",
                narrative = "Friendly traders share stories and warm food with the traveler.",
            ),
            finale(
                title = "Allies at the watchtower",
                narrative = "A band of guardians pledge to guard the traveler's village.",
            ),
            finale(
                title = "River spirits grant safe passage",
                narrative = "Gleaming figures guide the traveler safely home.",
            ),
        )
        // WHEN the traveler asks for an adventure
        whenAnAdventureIsRequested()
        // THEN the tale ends in three satisfying ways
        thenTheAdventureHasEndings(expected = 3)
        thenTheAdventureMentionsExactly(words = 4)
    }

    @Test
    fun `the guide corrects itself when the path would have continued past the limit`() = adventureScenario {
        // GIVEN the guide sometimes forgets to stop at the final step
        givenTheJourneyCanReachAtMost(depth = 1)
        givenTheGuideWillShare(
            chapter(
                title = "A whispering corridor",
                narrative = "An echo guides the traveler between ancient walls.",
                turn("Open the rune door", "Step into a firelit hall"),
            ),
            chapter(
                title = "Firelit hall",
                narrative = "Torches flare brighter as the traveler enters.",
                turn("Touch the floating ember", "Awaken a guardian"),
            ),
            finale(
                title = "The guardian bows",
                narrative = "The guardian grants safe passage and the corridor falls silent.",
            ),
        )
        // WHEN the traveler listens for the full tale
        whenAnAdventureIsRequested()
        // THEN the guide needed two attempts to finish, and it succeeded
        thenTheAdventureHasEndings(expected = 1)
        thenTheGuideSpoke(times = 3)
    }

    @Test
    fun `the journey fails when the guide speaks more times than allowed`() = adventureScenario {
        // GIVEN the traveler limits how often the guide may speak
        givenTheGuideCanSpeakOnly(times = 2)
        givenTheJourneyCanReachAtMost(depth = 2)
        givenTheGuideWillShare(
            chapter(
                title = "Into the mist",
                narrative = "Mist swirls around the traveler, muffling distant bells.",
                turn("Call out", "A voice answers from afar"),
            ),
            chapter(
                title = "Reply from the bells",
                narrative = "The bells swing wildly as if beckoning." ,
                turn("Step toward the bells", "A hidden village"),
            ),
            finale(
                title = "The bells quiet",
                narrative = "Villagers greet the traveler with warm lanterns.",
            ),
        )
        // WHEN the traveler asks for the tale
        whenAnAdventureIsRequested()
        // THEN the journey cannot finish because the guide was silenced too soon
        thenTheAdventureFailsWithMessage("Rate limit reached")
    }
}
