Feature: Text adventure

  Scenario: A text adventure can be played to completion
    Given a text adventure scenario "adventure-1" titled "Harbor Mist" starts with "You arrive at a quiet harbor." and translation "Llegas a un puerto tranquilo."
    And the text adventure response for "adventure-1" is "A lantern flickers on the dock." with translation "Una linterna parpadea en el muelle." and ending false
    And the text adventure response for "adventure-1" is "The fog lifts and the journey ends." with translation "La niebla se disipa y el viaje termina." and ending true
    And the reader starts a text adventure
    When the reader responds with "I walk toward the dock."
    Then the text adventure message "A lantern flickers on the dock." is shown
    When the reader translates sentence "A lantern flickers on the dock."
    Then the text adventure translation "Una linterna parpadea en el muelle." is shown
    When the reader responds with "I take the lantern."
    Then the text adventure message "The fog lifts and the journey ends." is shown
    And the text adventure input is hidden

  Scenario: An unfinished adventure can be resumed
    Given a text adventure scenario "adventure-2" titled "Forest Echoes" starts with "A trail winds into the forest." and translation "Un sendero se adentra en el bosque."
    And the text adventure response for "adventure-2" is "Birdsong follows you between the trees." with translation "El canto de los pájaros te sigue entre los árboles." and ending false
    And the reader starts a text adventure
    When the reader responds with "I follow the trail."
    And the reader returns to the story list
    Then the text adventure "Forest Echoes" is visible in the list
    When the reader opens text adventure "Forest Echoes"
    Then the text adventure message "A trail winds into the forest." is shown
    And the text adventure message "Birdsong follows you between the trees." is shown
    And the text adventure input is visible
