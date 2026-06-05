Feature: Story list

  Scenario: The story list shows the library
    Given the story library is available
    And the story list is open
    Then the first story is listed

  Scenario: Opening the first story shows its content
    Given the story library is available
    And the story list is open
    When the reader selects the first story
    Then the story reader shows the first story content in German

  Scenario: Opening the last story shows its content
    Given the story library is available
    And the story list is open
    When the reader scrolls to and selects the last story
    Then the story reader shows the last story content in German

  Scenario: Story images are shown in the list
    Given the story library is available
    And the story list is open
    Then the first story image is shown in the list

  Scenario: Stories without a featured image are filtered out
    Given the story library is available
    And the first story has no image
    And the story list is open
    Then the first story is not listed
    And the second story is listed

  @aiTextAdventuresDisabled
  Scenario: The story list opens with text adventures disabled
    Given the story library is available
    And the story list is open
    Then the first story is listed
    And the start text adventure button is hidden

  Scenario: Story content follows the learning language
    Given the story library is available
    And the story list is open
    When the reader sets the learning language to German
    And the reader sets the learning language to Spanish
    And the reader selects the first story learning Spanish
    Then the story reader shows the first story content in Spanish

  Scenario: Story titles follow the learning language
    Given the story library is available
    And the story list is open
    When the reader sets the learning language to German
    And the reader sets the learning language to Spanish
    Then the first story is listed in Spanish

  Scenario: Translations follow the translation language
    Given the story library is available
    And the story list is open
    When the reader sets the translation language to English
    And the reader sets the translation language to Spanish
    And the reader selects the first story
    And the reader taps the first German sentence of the first story
    Then the story reader shows the first story content in Spanish

  Scenario: Changing the translation language keeps the learning language separate
    Given the story library is available
    And the story list is open
    Then the learning language is German
    And the translation language is English
    When the reader sets the translation language to German
    Then the learning language is English
    And the translation language is German
    When the reader selects the first story learning English
    Then the story reader shows the first story title in English
    And the story reader shows the first story content in English
    When the reader taps the first English sentence of the first story
    Then the story reader shows the first story content in German

  Scenario: Changing the learning language keeps the translation language separate
    Given the story library is available
    And the story list is open
    When the reader sets the translation language to Spanish
    And the reader sets the learning language to Spanish
    Then the learning language is Spanish
    And the translation language is German
    When the reader selects the first story learning Spanish
    Then the story reader shows the first story title in Spanish
    And the story reader shows the first story content in Spanish
    When the reader taps the first Spanish sentence of the first story
    Then the story reader shows the first story content in German

  Scenario: Stories without a translation are hidden from the list
    Given the first two stories are available
    And the first story has no English translation
    And the story list is open
    Then the first story is not listed
    And the second story is listed
