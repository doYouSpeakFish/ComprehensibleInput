Feature: Settings

  Scenario: Settings displays software licences
    Given a reader is on the story list
    When the reader opens settings
    Then settings show the software licences option

  Scenario: Software licences opens from settings
    Given a reader is on the story list
    When the reader opens settings
    And the reader opens software licences
    Then software licences screen is shown

  Scenario: Navigate back from settings
    Given a reader is on the story list
    When the reader opens settings
    And the reader navigates back
    Then the story list is shown again
