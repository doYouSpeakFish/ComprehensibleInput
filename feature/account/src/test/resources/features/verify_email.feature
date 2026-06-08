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

  Scenario: The verify email error dialog can be dismissed
    Given the email verification screen for "user@example.com" is open
    And the verify email request will fail
    And I enter the verification code "123456"
    And I submit the verify email form
    And the error dialog is shown
    When I dismiss the error dialog
    Then the verify email submit button is enabled

  Scenario: A new verification code can be requested from the verify email screen
    Given the email verification screen for "user@example.com" is open
    Then the resend verification code button is enabled

  Scenario: The loading state is shown while a new verification code is requested
    Given the email verification screen for "user@example.com" is open
    And account requests are delayed
    And the email verification code request will succeed
    When I request a new verification code
    Then the resend verification code loading indicator is shown
    And the resend verification code button is disabled

  Scenario: A successful verification code request shows a confirmation message
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will succeed
    When I request a new verification code
    Then the verification code resent confirmation is shown

  Scenario: Requesting a new verification code clears the previously entered code
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will succeed
    And I enter the verification code "123456"
    When I request a new verification code
    Then the verify email submit button is disabled

  Scenario: A failed verification code request shows the error dialog
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will fail
    When I request a new verification code
    Then the error dialog is shown

  Scenario: A failed verification code request still starts the resend cooldown
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will fail
    When I request a new verification code
    Then the error dialog is shown
    When I dismiss the error dialog
    Then the resend verification code button is disabled
    And the resend verification code button shows a 30 second countdown

  Scenario: The resend button is disabled with a countdown after a new code is requested
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will succeed
    When I request a new verification code
    Then the resend verification code button is disabled
    And the resend verification code button shows a 30 second countdown

  Scenario: The resend countdown counts down the remaining seconds
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will succeed
    When I request a new verification code
    And 1 second passes
    Then the resend verification code button shows a 29 second countdown

  Scenario: The resend button is re-enabled once the cooldown elapses
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will succeed
    When I request a new verification code
    And the resend code cooldown elapses
    Then the resend verification code button is enabled

  Scenario: A new code can be requested again once the cooldown has elapsed
    Given the email verification screen for "user@example.com" is open
    And the email verification code request will succeed
    And the email verification code request will succeed
    When I request a new verification code
    And the resend code cooldown elapses
    And I request a new verification code
    Then the resend verification code button is disabled
    And the resend verification code button shows a 30 second countdown
