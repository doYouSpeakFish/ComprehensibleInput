Feature: Text adventure

  Scenario: A text adventure can be played to completion
    Given the "Harbor Mist" text adventure is available
    And the story list is open
    When the reader starts a new text adventure
    Then the text adventure shows "You arrive at a quiet harbor."
    When the reader responds with "I walk toward the dock."
    Then the text adventure shows "A lantern flickers on the dock."
    When the reader taps "A lantern flickers on the dock." to translate it
    Then the text adventure shows "Una linterna parpadea en el muelle."
    When the reader responds with "I take the lantern."
    Then the text adventure shows "The fog lifts and the journey ends."
    And the text adventure input is hidden

  Scenario: An unfinished adventure can be resumed
    Given the "Forest Echoes" text adventure is available
    And the story list is open
    When the reader starts a new text adventure
    And the reader responds with "I follow the trail."
    And the reader returns to the story list
    Then the "Forest Echoes" text adventure is listed
    When the reader resumes the "Forest Echoes" text adventure
    Then the text adventure shows "A trail winds into the forest."
    And the text adventure shows "Birdsong follows you between the trees."
    And the text adventure input is visible
