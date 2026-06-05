Feature: Settings

  Scenario: The settings screen shows the account option
    Given the settings screen is open
    Then the account option is shown

  Scenario: The account option opens the account screen
    Given the settings screen is open
    When the reader opens the account option
    Then the account screen title is shown

  @accountManagementDisabled
  Scenario: The account option is hidden when account management is disabled
    Given the settings screen is open
    Then the account option is not shown

  Scenario: The settings screen shows the software licences option
    Given the settings screen is open
    Then the settings title is shown
    And the software licences option is shown

  Scenario: The software licences option opens the software licences screen
    Given the settings screen is open
    When the reader opens the software licences option
    Then the software licences title is shown

  Scenario: Settings can be opened from the story list
    Given the story library is available
    And the story list is open
    When the reader opens the settings screen
    Then the settings title is shown

  Scenario: Navigating up from settings returns to the story list
    Given the story library is available
    And the story list is open
    When the reader opens the settings screen
    And the reader navigates up from settings
    Then the learning language is German
