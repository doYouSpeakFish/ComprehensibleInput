Feature: Verify email

  Scenario: Submit is disabled when no code has been entered
    Given the email verification screen for "user@example.com" is open
    Then the verify email submit button is disabled

  Scenario: Submit is disabled when the code has fewer than six digits
    Given the email verification screen for "user@example.com" is open
    When I enter the verification code "123"
    Then the verify email submit button is disabled

  Scenario: Submit is enabled when a six digit code is entered
    Given the email verification screen for "user@example.com" is open
    When I enter the verification code "123456"
    Then the verify email submit button is enabled

  Scenario: The loading state is shown while the request is in progress
    Given the email verification screen for "user@example.com" is open
    And account requests are delayed
    And the verify email request will succeed
    When I enter the verification code "123456"
    And I submit the verify email form
    Then the verify email loading indicator is shown
    And the verify email submit button is disabled

  Scenario: A successful verification returns to the account sign in screen
    Given the account screen is open
    And the email verification screen for "user@example.com" is open
    And the verify email request will succeed
    When I enter the verification code "123456"
    And I submit the verify email form
    Then the account sign in screen is shown

  Scenario: A failed verification shows the error dialog
    Given the email verification screen for "user@example.com" is open
    And the verify email request will fail
    When I enter the verification code "123456"
    And I submit the verify email form
    Then the error dialog is shown
