Feature: Sign in

  Scenario: The sign in screen is shown when no session exists
    Given the account screen is open
    Then the account title is shown
    And the account sign in screen is shown

  Scenario: Submit is disabled when the fields are empty
    Given the account screen is open
    Then the sign in submit button is disabled

  Scenario: Submit is disabled when only the email is filled
    Given the account screen is open
    When I enter the sign in email "user@example.com"
    Then the sign in submit button is disabled

  Scenario: Submit is disabled when only the password is filled
    Given the account screen is open
    When I enter the sign in password "password12345"
    Then the sign in submit button is disabled

  Scenario: Submit is enabled when the email and password are filled
    Given the account screen is open
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    Then the sign in submit button is enabled

  Scenario: The loading indicator is shown while the request is in progress
    Given the account screen is open
    And account requests are delayed
    And the sign in request will succeed
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the sign in loading indicator is shown

  Scenario: Submit is disabled while the request is in progress
    Given the account screen is open
    And account requests are delayed
    And the sign in request will succeed
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the sign in submit button is disabled

  Scenario: The sign up button is disabled while signing in is in progress
    Given the account screen is open
    And account requests are delayed
    And the sign in request will succeed
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the sign up button is disabled

  Scenario: A successful sign in shows the signed in state
    Given the account screen is open
    And the sign in request will succeed
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the signed in email "user@example.com" is shown

  Scenario: A failed sign in shows the invalid credentials dialog
    Given the account screen is open
    And the sign in request will fail with invalid credentials
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "wrongpassword12345"
    And I submit the sign in form
    Then the sign in invalid credentials dialog is shown

  Scenario: A non-credentials failure shows the error dialog
    Given the account screen is open
    And the sign in request will fail
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the error dialog is shown

  Scenario: The sign in error dialog can be dismissed
    Given the account screen is open
    And the sign in request will fail
    And I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    And the error dialog is shown
    When I dismiss the error dialog
    Then the sign in submit button is enabled

  Scenario: The sign in invalid credentials dialog can be dismissed
    Given the account screen is open
    And the sign in request will fail with invalid credentials
    And I enter the sign in email "user@example.com"
    And I enter the sign in password "wrongpassword12345"
    And I submit the sign in form
    And the sign in invalid credentials dialog is shown
    When I dismiss the sign in invalid credentials dialog
    Then the sign in submit button is enabled

  Scenario: The sign up button opens the sign up screen
    Given the account screen is open
    When I tap the sign up button
    Then the sign up submit button is disabled
