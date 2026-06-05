Feature: Delete account

  Scenario: Submit is disabled when the password is empty
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    Then the delete account submit button is disabled

  Scenario: Submit is enabled when the password is entered
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    When I enter the delete account password "password12345"
    Then the delete account submit button is enabled

  Scenario: The loading state is shown while the request is in progress
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    And account requests are delayed
    And the delete account request will succeed
    When I enter the delete account password "password12345"
    And I submit the delete account form
    Then the delete account loading indicator is shown
    And the delete account submit button is disabled

  Scenario: A successful deletion returns to the sign in screen
    Given I am signed in as "user@example.com"
    And the account screen is open
    And the delete account screen is open
    And the delete account request will succeed
    When I enter the delete account password "password12345"
    And I submit the delete account form
    Then the account sign in screen is shown

  Scenario: A wrong password shows the invalid credentials dialog
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    And the delete account request will fail with invalid credentials
    When I enter the delete account password "wrongpassword"
    And I submit the delete account form
    Then the delete account invalid credentials dialog is shown

  Scenario: A generic failure shows the error dialog
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    And the delete account request will fail
    When I enter the delete account password "password12345"
    And I submit the delete account form
    Then the error dialog is shown

  Scenario: The error dialog can be dismissed
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    And the delete account request will fail
    And I enter the delete account password "password12345"
    And I submit the delete account form
    And the error dialog is shown
    When I dismiss the error dialog
    Then the delete account submit button is enabled

  Scenario: The invalid credentials dialog can be dismissed
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    And the delete account request will fail with invalid credentials
    And I enter the delete account password "wrongpassword"
    And I submit the delete account form
    And the delete account invalid credentials dialog is shown
    When I dismiss the delete account invalid credentials dialog
    Then the delete account submit button is enabled

  Scenario: A failed deletion keeps the session
    Given I am signed in as "user@example.com"
    And the delete account screen is open
    And the delete account request will fail with invalid credentials
    When I enter the delete account password "wrongpassword"
    And I submit the delete account form
    And the account screen is open
    Then the signed in email "user@example.com" is shown
