Feature: Forgot password

  Scenario: Tapping forgot password opens the forgot password screen
    Given the account screen is open
    When I tap the forgot password button
    Then the forgot password submit button is disabled

  Scenario: Submit is disabled when the email is empty
    Given the forgot password screen is open
    Then the forgot password submit button is disabled

  Scenario: Submit is disabled when the email is invalid
    Given the forgot password screen is open
    When I enter the forgot password email "invalidemail"
    Then the forgot password submit button is disabled

  Scenario: Submit is enabled when the email is valid
    Given the forgot password screen is open
    When I enter the forgot password email "user@example.com"
    Then the forgot password submit button is enabled

  Scenario: The loading state is shown while the request is in progress
    Given the forgot password screen is open
    And account requests are delayed
    And the password reset code request will succeed
    When I enter the forgot password email "user@example.com"
    And I submit the forgot password form
    Then the forgot password loading indicator is shown
    And the forgot password submit button is disabled

  Scenario: A successful request opens the password reset screen
    Given the forgot password screen is open
    And the password reset code request will succeed
    When I enter the forgot password email "user@example.com"
    And I submit the forgot password form
    Then the password reset screen shows the reset code message for "user@example.com"

  Scenario: A failed request shows the error dialog
    Given the forgot password screen is open
    And the password reset code request will fail
    When I enter the forgot password email "user@example.com"
    And I submit the forgot password form
    Then the error dialog is shown

  Scenario: The error dialog can be dismissed
    Given the forgot password screen is open
    And the password reset code request will fail
    And I enter the forgot password email "user@example.com"
    And I submit the forgot password form
    And the error dialog is shown
    When I dismiss the error dialog
    Then the forgot password submit button is enabled
