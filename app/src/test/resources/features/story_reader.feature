Feature: Story reader

  Scenario: Story title and content are shown
    Given a full story library is loaded for story reader
    When the reader opens the first story reader page
    Then the first story title is shown in reader
    And the first story German text is shown in reader

  Scenario: Story text can be translated to English
    Given a full story library is loaded for story reader
    And the reader opens the first story reader page
    When the reader taps the first German sentence in reader
    Then the first story English sentence is shown in reader

  Scenario: Loading indicator is shown while story loads
    Given a full story library is loaded for story reader
    And story loading is delayed by 2000 milliseconds
    When the reader opens the first story reader page
    Then the story loading indicator is shown

  Scenario: Error dialog is shown when translation is missing
    Given a full story library is loaded for story reader
    And the first story is missing English translation
    When the reader opens the first story reader page
    Then the story error dialog is shown
