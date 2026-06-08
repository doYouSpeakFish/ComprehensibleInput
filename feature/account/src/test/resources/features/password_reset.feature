Feature: Password reset

  Scenario: Submit is disabled when the fields are empty
    Given the password reset screen for "user@example.com" is open
    Then the password reset submit button is disabled

  Scenario: Submit is disabled when the code is too short
    Given the password reset screen for "user@example.com" is open
    When I enter the reset code "123"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    Then the password reset submit button is disabled

  Scenario: Submit is disabled when the password is too short
    Given the password reset screen for "user@example.com" is open
    When I enter the reset code "123456"
    And I enter the new password "short"
    And I enter the new confirmation password "short"
    Then the password reset submit button is disabled

  Scenario: Submit is disabled when the passwords do not match
    Given the password reset screen for "user@example.com" is open
    When I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "differentpassword12345"
    Then the password reset submit button is disabled

  Scenario: Submit is enabled when all fields are valid
    Given the password reset screen for "user@example.com" is open
    When I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    Then the password reset submit button is enabled

  Scenario: The loading state is shown while the request is in progress
    Given the password reset screen for "user@example.com" is open
    And account requests are delayed
    And the password reset request will succeed
    And I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    When I submit the password reset form
    Then the password reset loading indicator is shown
    And the password reset submit button is disabled

  Scenario: A successful reset returns to the account sign in screen
    Given the account screen is open
    And the password reset screen for "user@example.com" is open
    And the password reset request will succeed
    And I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    When I submit the password reset form
    Then the account sign in screen is shown

  Scenario: A failed reset shows the error dialog
    Given the password reset screen for "user@example.com" is open
    And the password reset request will fail
    And I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    When I submit the password reset form
    Then the error dialog is shown

  Scenario: The error dialog can be dismissed
    Given the password reset screen for "user@example.com" is open
    And the password reset request will fail
    And I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    And I submit the password reset form
    And the error dialog is shown
    When I dismiss the error dialog
    Then the password reset submit button is enabled

  Scenario: An invalid code shows the invalid reset code dialog
    Given the password reset screen for "user@example.com" is open
    And the password reset request will fail with an invalid code
    And I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    When I submit the password reset form
    Then the invalid reset code dialog is shown

  Scenario: The invalid reset code dialog can be dismissed
    Given the password reset screen for "user@example.com" is open
    And the password reset request will fail with an invalid code
    And I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    And I submit the password reset form
    And the invalid reset code dialog is shown
    When I dismiss the invalid reset code dialog
    Then the password reset submit button is enabled

  Scenario: A new reset code can be requested from the password reset screen
    Given the password reset screen for "user@example.com" is open
    Then the resend reset code button is enabled

  Scenario: The loading state is shown while a new reset code is requested
    Given the password reset screen for "user@example.com" is open
    And account requests are delayed
    And the password reset code request will succeed
    When I request a new reset code
    Then the resend reset code loading indicator is shown
    And the resend reset code button is disabled

  Scenario: A successful reset code request shows a confirmation message
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will succeed
    When I request a new reset code
    Then the reset code resent confirmation is shown

  Scenario: Requesting a new reset code clears the previously entered code
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will succeed
    And I enter the reset code "123456"
    And I enter the new password "newpassword12345"
    And I enter the new confirmation password "newpassword12345"
    When I request a new reset code
    Then the password reset submit button is disabled

  Scenario: A failed reset code request shows the error dialog
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will fail
    When I request a new reset code
    Then the error dialog is shown

  Scenario: The reset code request error dialog can be dismissed
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will fail
    When I request a new reset code
    Then the error dialog is shown
    When I dismiss the error dialog
    Then the resend reset code button is enabled

  Scenario: The resend button is disabled with a countdown after a new code is requested
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will succeed
    When I request a new reset code
    Then the resend reset code button is disabled
    And the resend reset code button shows a 30 second countdown

  Scenario: The resend countdown counts down the remaining seconds
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will succeed
    When I request a new reset code
    And 1 second passes
    Then the resend reset code button shows a 29 second countdown

  Scenario: The resend button is re-enabled once the cooldown elapses
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will succeed
    When I request a new reset code
    And the resend code cooldown elapses
    Then the resend reset code button is enabled

  Scenario: A new code can be requested again once the cooldown has elapsed
    Given the password reset screen for "user@example.com" is open
    And the password reset code request will succeed
    And the password reset code request will succeed
    When I request a new reset code
    And the resend code cooldown elapses
    And I request a new reset code
    Then the resend reset code button is disabled
    And the resend reset code button shows a 30 second countdown

  Scenario: The new password is hidden by default
    Given the password reset screen for "user@example.com" is open
    When I enter the new password "newpassword12345"
    Then the new password is hidden

  Scenario: The new password can be revealed
    Given the password reset screen for "user@example.com" is open
    When I enter the new password "newpassword12345"
    And I reveal the new password
    Then the new password is shown

  Scenario: The new confirmation password can be revealed
    Given the password reset screen for "user@example.com" is open
    When I enter the new confirmation password "newpassword12345"
    And I reveal the new confirmation password
    Then the new confirmation password is shown

  Scenario: Revealing the new password leaves the new confirmation password hidden
    Given the password reset screen for "user@example.com" is open
    When I reveal the new password
    Then the new password is shown
    And the new confirmation password is hidden
