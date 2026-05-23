Feature: Story list

  Scenario: First story can be selected
    Given a full story library is loaded
    And the reader is on the story list screen
    When the reader opens the first story
    Then the first story text is shown

  Scenario: Story image is shown in list
    Given a full story library is loaded
    And the reader is on the story list screen
    Then the first story image is visible

  Scenario: Stories without images are filtered out
    Given a full story library is loaded
    And the first story has no image
    When the reader opens the story list screen
    Then the first story is not visible in the list

  Scenario: Story list opens when text adventures are disabled
    Given a full story library is loaded with text adventures disabled
    When the reader opens the story list screen
    Then the text adventure call to action is hidden

  Scenario: Story content follows learning language
    Given a full story library is loaded
    And the reader is on the story list screen
    When the learning language is set to "es"
    And the reader opens the first story in language "es"
    Then the first story text is shown in language "es"
