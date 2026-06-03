Feature: Account management API

  Scenario: Creating a user
    When I create user with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200
    And an email should be sent to "alice@example.com" containing "Comprehensible Input"

  Scenario: Rejecting duplicate user creation when email is verified
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    When I create user with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200
    And an email should be sent to "alice@example.com" containing "already exists"

  Scenario: Re-sending verification code when existing account email is not verified
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "654321"
    When I create user with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200
    And an email should be sent to "alice@example.com" containing "654321"
    When I verify email "alice@example.com" using code "654321"
    Then account API status should be 204

  Scenario: Rejecting invalid user creation
    When I create user with email "bad" and password "short"
    Then account API status should be 400

  Scenario: Signing in requires verified email
    Given existing user "alice@example.com" with password "SecurePass123!"
    When I sign in with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 401

  Scenario: Verifying email with valid code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    When I verify email "alice@example.com" using code "123456"
    Then account API status should be 204

  Scenario: Signing in after email is verified
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    When I sign in with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200

  Scenario: Rejecting verification with wrong code using generic error
    Given existing user "alice@example.com" with password "SecurePass123!"
    When I verify email "alice@example.com" using code "000000"
    Then account API status should be 400

  Scenario: Rejecting verification for unknown email using generic error
    When I verify email "missing@example.com" using code "000000"
    Then account API status should be 400

  Scenario: Verifying email fails with expired code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And time advances by 16 minutes and 0 seconds
    And the next verification code will be "123456"
    When I verify email "alice@example.com" using code "123456"
    Then account API status should be 400

  Scenario: Verification codes are single use
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    Then account API status should be 204
    When I verify email "alice@example.com" using code "123456"
    Then account API status should be 400

  Scenario: Rejecting sign in with wrong password
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    When I sign in with email "alice@example.com" and password "WrongPass123!"
    Then account API status should be 401

  Scenario: Rejecting sign in with unknown email
    When I sign in with email "missing@example.com" and password "SecurePass123!"
    Then account API status should be 401

  Scenario: Getting me
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I request me profile
    Then account API status should be 200

  Scenario: Rejecting me without token
    When I request me profile without authorization
    Then account API status should be 401

  Scenario: Rejecting me update without token
    When I update me email to "alice2@example.com" without authorization
    Then account API status should be 401

  Scenario: Updating me
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I update me email to "alice2@example.com"
    Then account API status should be 200
    And an email should be sent to "alice@example.com" containing "unexpected"
    When I request me profile
    Then account API status should be 200
    And account profile email should be "alice@example.com"
    When I verify current email change using code "123456"
    Then account API status should be 204
    And an email should be sent to "alice2@example.com" containing "verification code"
    And an email should be sent to "alice2@example.com" containing "123456"
    When I verify pending email change to "alice2@example.com" using code "123456"
    Then account API status should be 204
    When I request me profile
    Then account API status should be 401
    When I sign in with email "alice2@example.com" and password "SecurePass123!"
    Then account API status should be 200

  Scenario: Rejecting me update to duplicate email
    Given existing user "alice@example.com" with password "SecurePass123!"
    And existing user "bob@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I update me email to "bob@example.com"
    Then account API status should be 200
    And an email should be sent to "bob@example.com" containing "already has"

  Scenario: Rejecting pending email verification with wrong code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I update me email to "alice2@example.com"
    Then account API status should be 200
    When I verify pending email change to "alice2@example.com" using code "999999"
    Then account API status should be 400

  Scenario: Deleting me
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I delete me
    Then account API status should be 204

  Scenario: Rejecting me deletion with wrong password
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I delete me with wrong password
    Then account API status should be 401

  Scenario: Rejecting repeated me deletion due to rate limiting
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I attempt to delete me a second time
    Then account API status should be 429

  Scenario: Rate limiting account deletion by IP when JSON body is malformed
    When I attempt to delete me a second time with malformed body
    Then account API status should be 429

  Scenario: Rate limiting account deletion by X-Forwarded-For when JSON body is malformed
    When I attempt to delete me a second time with malformed body and forwarded IP
    Then account API status should be 429

  Scenario: Rate limiting email verification by email in query parameter
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    When I attempt to verify email a second time with email in query parameter
    Then account API status should be 429

  Scenario: Rate limiting email verification by X-Forwarded-For when email is not in query parameter
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    When I attempt to verify email a second time with forwarded IP
    Then account API status should be 429

  Scenario: Signing out current session
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I sign out current session
    Then account API status should be 204

  Scenario: Rejecting sign out with invalid token
    When I sign out current session with invalid token
    Then account API status should be 401

  Scenario: Rejecting sign out without token
    When I sign out current session without token
    Then account API status should be 401

  Scenario: Requesting a password reset code for an existing account
    Given existing user "alice@example.com" with password "SecurePass123!"
    When I request a password reset code for "alice@example.com"
    Then account API status should be 202
    And an email should be sent to "alice@example.com" containing "password reset code"

  Scenario: Requesting a password reset code for an unknown account still succeeds
    When I request a password reset code for "missing@example.com"
    Then account API status should be 202
    And an email should be sent to "missing@example.com" containing "do not have"

  Scenario: Requesting a password reset code again replaces the previous code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "111111"
    And I request a password reset code for "alice@example.com"
    And the next verification code will be "222222"
    When I request a password reset code for "alice@example.com"
    Then account API status should be 202
    When I reset password for "alice@example.com" to "NewSecurePass123!" using code "111111"
    Then account API status should be 400
    When I reset password for "alice@example.com" to "NewSecurePass123!" using code "222222"
    Then account API status should be 204

  Scenario: Resetting password succeeds with valid code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    And the next verification code will be "123456"
    And I request a password reset code for "alice@example.com"
    When I reset password for "alice@example.com" to "NewSecurePass123!" using code "123456"
    Then account API status should be 204
    When I request me profile
    Then account API status should be 401
    When I sign in with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 401
    When I sign in with email "alice@example.com" and password "NewSecurePass123!"
    Then account API status should be 200

  Scenario: Resetting password fails with expired code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I request a password reset code for "alice@example.com"
    And time advances by 16 minutes and 0 seconds
    When I reset password for "alice@example.com" to "NewSecurePass123!" using code "123456"
    Then account API status should be 400


  Scenario: Rejecting current email verification with wrong code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I update me email to "alice2@example.com"
    Then account API status should be 200
    When I verify current email change using code "999999"
    Then account API status should be 400

  Scenario: Requesting a new email verification code sends a fresh code to an unverified account
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "654321"
    When I request a new email verification code for "alice@example.com"
    Then account API status should be 202
    And an email should be sent to "alice@example.com" containing "654321"
    When I verify email "alice@example.com" using code "654321"
    Then account API status should be 204

  Scenario: Requesting a new email verification code replaces the previous code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "111111"
    And I request a new email verification code for "alice@example.com"
    And the next verification code will be "222222"
    When I request a new email verification code for "alice@example.com"
    Then account API status should be 202
    When I verify email "alice@example.com" using code "111111"
    Then account API status should be 400
    When I verify email "alice@example.com" using code "222222"
    Then account API status should be 204

  Scenario: Requesting a new email verification code for an unknown email still succeeds
    When I request a new email verification code for "missing@example.com"
    Then account API status should be 202

  Scenario: Requesting a new email verification code for an already verified account still succeeds
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    When I request a new email verification code for "alice@example.com"
    Then account API status should be 202

  Scenario: Requesting a new email change current verification code sends a fresh code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    And I update me email to "alice2@example.com"
    And the next verification code will be "654321"
    When I request a new email change current verification code
    Then account API status should be 202
    And an email should be sent to "alice@example.com" containing "654321"
    When I verify current email change using code "654321"
    Then account API status should be 204

  Scenario: Requesting a new email change current verification code replaces the previous code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    And I update me email to "alice2@example.com"
    And the next verification code will be "111111"
    And I request a new email change current verification code
    And the next verification code will be "222222"
    When I request a new email change current verification code
    Then account API status should be 202
    When I verify current email change using code "111111"
    Then account API status should be 400
    When I verify current email change using code "222222"
    Then account API status should be 204

  Scenario: Requesting a new email change current verification code without a pending email change fails
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I request a new email change current verification code
    Then account API status should be 400

  Scenario: Requesting a new email change current verification code without authorization fails
    When I request a new email change current verification code without authorization
    Then account API status should be 401

  Scenario: Requesting a new email change new-email verification code sends a fresh code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    And I update me email to "alice2@example.com"
    And I verify current email change using code "123456"
    And the next verification code will be "654321"
    When I request a new email change new-email verification code
    Then account API status should be 202
    And an email should be sent to "alice2@example.com" containing "654321"
    When I verify pending email change to "alice2@example.com" using code "654321"
    Then account API status should be 204

  Scenario: Requesting a new email change new-email verification code replaces the previous code
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    And I update me email to "alice2@example.com"
    And I verify current email change using code "123456"
    And the next verification code will be "111111"
    And I request a new email change new-email verification code
    And the next verification code will be "222222"
    When I request a new email change new-email verification code
    Then account API status should be 202
    When I verify pending email change to "alice2@example.com" using code "111111"
    Then account API status should be 400
    When I verify pending email change to "alice2@example.com" using code "222222"
    Then account API status should be 204

  Scenario: Requesting a new email change new-email verification code without a pending email change in the correct state fails
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I request a new email change new-email verification code
    Then account API status should be 400

  Scenario: Requesting a new email change new-email verification code without authorization fails
    When I request a new email change new-email verification code without authorization
    Then account API status should be 401
