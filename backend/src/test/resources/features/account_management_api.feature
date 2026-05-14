Feature: Account management API

  Scenario: Creating a user
    When I create user with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200
    And an email should be sent to "alice@example.com" containing "Comprehensible Input"

  Scenario: Rejecting duplicate user creation
    Given existing user "alice@example.com" with password "SecurePass123!"
    When I create user with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200
    And an email should be sent to "alice@example.com" containing "already exists"

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

  Scenario: Rejecting me update to duplicate email
    Given existing user "alice@example.com" with password "SecurePass123!"
    And existing user "bob@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I update me email to "bob@example.com"
    Then account API status should be 409

  Scenario: Deleting me
    Given existing user "alice@example.com" with password "SecurePass123!"
    And the next verification code will be "123456"
    And I verify email "alice@example.com" using code "123456"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I delete me
    Then account API status should be 204

  Scenario: Rejecting me deletion without token
    When I delete me without authorization
    Then account API status should be 401

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
