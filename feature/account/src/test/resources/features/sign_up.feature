Feature: Sign up

  Scenario: Submit is disabled when the form is empty
    Given the sign up screen is open
    Then the sign up submit button is disabled

  Scenario: Submit is disabled when the email is missing
    Given the sign up screen is open
    When I enter the sign up password "password12345"
    And I enter the sign up confirmation password "password12345"
    Then the sign up submit button is disabled

  Scenario: Submit is disabled when the email is invalid
    Given the sign up screen is open
    When I enter the sign up email "invalidemail"
    And I enter the sign up password "password12345"
    And I enter the sign up confirmation password "password12345"
    Then the sign up submit button is disabled

  Scenario: Submit is disabled when the password is too short
    Given the sign up screen is open
    When I enter the sign up email "user@example.com"
    And I enter the sign up password "short"
    And I enter the sign up confirmation password "short"
    Then the sign up submit button is disabled

  Scenario: Submit is disabled when the passwords do not match
    Given the sign up screen is open
    When I enter the sign up email "user@example.com"
    And I enter the sign up password "password12345"
    And I enter the sign up confirmation password "differentpassword12345"
    Then the sign up submit button is disabled

  Scenario: Submit is enabled when all fields are valid
    Given the sign up screen is open
    When I enter the sign up email "user@example.com"
    And I enter the sign up password "password12345"
    And I enter the sign up confirmation password "password12345"
    Then the sign up submit button is enabled

  Scenario: The loading state is shown while the request is in progress
    Given the sign up screen is open
    And account requests are delayed
    And the create account request will succeed
    And the sign up form is completed for "user@example.com"
    When I submit the sign up form
    Then the sign up loading indicator is shown
    And the sign up submit button is disabled

  Scenario: A successful sign up shows the email verification screen
    Given the sign up screen is open
    And the create account request will succeed
    And the sign up form is completed for "user@example.com"
    When I submit the sign up form
    Then the email verification screen shows the email "user@example.com"

  Scenario: A failed sign up shows the error dialog
    Given the sign up screen is open
    And the create account request will fail
    And the sign up form is completed for "user@example.com"
    When I submit the sign up form
    Then the error dialog is shown

  Scenario: The sign up error dialog can be dismissed
    Given the sign up screen is open
    And the create account request will fail
    And the sign up form is completed for "user@example.com"
    And I submit the sign up form
    And the error dialog is shown
    When I dismiss the error dialog
    Then the sign up submit button is enabled
