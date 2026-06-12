Feature: Signed in account

  Scenario: The signed in state is shown when a session exists
    Given I am signed in as "user@example.com"
    And the account screen is open
    Then the signed in email "user@example.com" is shown

  Scenario: Signing out returns to the sign in screen
    Given I am signed in as "user@example.com"
    And the account screen is open
    When I tap the sign out button
    Then the account sign in screen is shown

  Scenario: The delete account button is shown when signed in
    Given I am signed in as "user@example.com"
    And the account screen is open
    Then the delete account button is shown

  Scenario: The delete account button opens the delete account screen
    Given I am signed in as "user@example.com"
    And the account screen is open
    When I tap the delete account button
    Then the delete account explainer is shown
    And the delete account warning is shown
