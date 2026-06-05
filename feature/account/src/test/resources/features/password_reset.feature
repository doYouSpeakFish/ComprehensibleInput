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
