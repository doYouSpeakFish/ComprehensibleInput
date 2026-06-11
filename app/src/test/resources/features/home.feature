Feature: Home

  # Home is the start destination. It offers two options, "Stories" and
  # "Text Adventures", plus a settings action in the top bar. The text adventures
  # option is gated behind the aiTextAdventuresEnabled feature flag.

  Scenario: The home screen shows the available options
    Given the home screen is open
    Then the stories option is shown
    And the text adventures option is shown
    And the settings action is shown

  Scenario: Selecting stories opens the story library
    Given the story library is available
    And the home screen is open
    When I select the stories option
    Then the first story is listed

  Scenario: Selecting settings opens the settings screen
    Given the home screen is open
    When I select the settings action
    Then the settings title is shown

  @aiTextAdventuresDisabled
  Scenario: The text adventures option is hidden when the feature flag is disabled
    Given the home screen is open
    Then the stories option is shown
    And the text adventures option is hidden

  Scenario: Selecting text adventures opens the text adventures screen
    Given I am signed in as "user@example.com"
    And the home screen is open
    When I select the text adventures option
    Then the text adventures screen is shown

  Scenario: Navigating up from the text adventures screen returns home
    Given I am signed in as "user@example.com"
    And the home screen is open
    When I select the text adventures option
    And I navigate up from the text adventures screen
    Then the stories option is shown
