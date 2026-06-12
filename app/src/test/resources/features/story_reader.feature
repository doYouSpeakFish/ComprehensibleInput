Feature: Story reader

  Scenario: The story reader shows a story's title, content and image
    Given the story library is available
    And the first story is open in the reader
    Then the story reader shows the first story title in German
    And the story reader shows the first story content in German
    And the first story image is shown in the reader

  Scenario: The story title is shown
    Given the story library is available
    And the first story is open in the reader
    Then the story reader shows the first story title in German

  Scenario: The story content is shown
    Given the story library is available
    And the last story is open in the reader
    Then the story reader shows the last story content in German

  Scenario: The first image is shown
    Given the story library is available
    And the first story is open in the reader
    Then the first story image is shown in the reader

  Scenario: A sentence can be switched from German to English
    Given the story library is available
    And the first story is open in the reader
    When the reader taps the first German sentence of the first story
    Then the story reader shows the first story content in English

  Scenario: A sentence can be switched from English back to German
    Given the story library is available
    And the first story is open in the reader
    When the reader taps the first German sentence of the first story
    And the reader taps the first English sentence of the first story
    Then the story reader shows the first story content in German

  Scenario: A choice can be switched from German to English
    Given the choose your own adventure story is available
    And the choose your own adventure story is open in the reader
    When the reader taps the "keep the key" choice in German
    Then the "keep the key" choice is shown in English

  Scenario: A choice can be switched from English back to German
    Given the choose your own adventure story is available
    And the choose your own adventure story is open in the reader
    When the reader taps the "keep the key" choice in German
    And the reader taps the "keep the key" choice in English
    Then the "keep the key" choice is shown in German

  Scenario: Choosing a path opens the next part
    Given the choose your own adventure story is available
    And the choose your own adventure story is open in the reader
    Then the story reader shows the "start" part of the adventure in German
    When the reader chooses to keep the key
    Then the story reader shows the "keep the key" part of the adventure in German

  Scenario: Choices stay available when paging backwards
    Given the choose your own adventure story is available
    And the choose your own adventure story is open in the reader
    When the reader chooses to keep the key
    And the reader pages back to the previous part
    Then the "keep the key" choice is shown in German
    And the "return the key" choice is shown in German
    When the reader chooses to return the key
    Then the story reader shows the "return the key" part of the adventure in German

  Scenario: A title can be switched from German to English
    Given the story library is available
    And the first story is open in the reader
    When the reader taps the first story title in German
    Then the story reader shows the first story title in English

  Scenario: A title can be switched from English back to German
    Given the story library is available
    And the first story is open in the reader
    When the reader taps the first story title in German
    And the reader taps the first story title in English
    Then the story reader shows the first story title in German

  Scenario: A partially read story reopens at the saved position
    Given the story library is available
    And the first story is open in the reader
    When the reader skips ahead in the first story
    And the reader closes the story
    And the first story is open in the reader
    Then the story reader shows the saved position in the first story

  Scenario: The chosen path is remembered
    Given the choose your own adventure story is available
    And the choose your own adventure story is open in the reader
    Then the story reader shows the "start" part of the adventure in German
    When the reader chooses to keep the key
    Then the story reader shows the "keep the key" part of the adventure in German
    When the reader closes the story
    And the choose your own adventure story is open in the reader
    Then the story reader shows the "keep the key" part of the adventure in German
    When the reader pages back to the previous part
    Then the story reader shows the "start" part of the adventure in German

  Scenario: A loading indicator is shown while the story loads
    Given the story library is available
    And story loads are delayed
    And the first story is open in the reader
    Then the story reader shows a loading indicator

  Scenario: An error is shown when the story is missing in the learning language
    Given the story library is available
    And the first story has no German learning content
    And the story list is open
    When the reader sets the learning language to German
    And the reader sets the translation language to English
    And the first story is open in the reader
    Then the story reader shows an error
    When the reader dismisses the story error
    Then the learning language is German

  Scenario: An error is shown when the translation is missing
    Given the story library is available
    And the first story has no English translation
    And the first story is open in the reader
    Then the story reader shows an error

  Scenario: An error is shown when the translation sentences do not match
    Given the story library is available
    And the first story has a mismatched English translation
    And the first story is open in the reader
    Then the story reader shows an error

  Scenario: An error is shown when an image cannot be loaded
    Given the story library is available
    And the first story has no image
    And the first story is open in the reader
    Then the story reader shows an error
