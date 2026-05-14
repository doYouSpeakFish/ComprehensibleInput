Feature: Account management API

  Scenario: Creating a user
    When I create user with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200

  Scenario: Rejecting duplicate user creation
    Given existing user "alice@example.com" with password "SecurePass123!"
    When I create user with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200

  Scenario: Rejecting invalid user creation
    When I create user with email "bad" and password "short"
    Then account API status should be 400

  Scenario: Signing in
    Given existing user "alice@example.com" with password "SecurePass123!"
    When I sign in with email "alice@example.com" and password "SecurePass123!"
    Then account API status should be 200

  Scenario: Rejecting sign in with wrong password
    Given existing user "alice@example.com" with password "SecurePass123!"
    When I sign in with email "alice@example.com" and password "WrongPass123!"
    Then account API status should be 401

  Scenario: Rejecting sign in with unknown email
    When I sign in with email "missing@example.com" and password "SecurePass123!"
    Then account API status should be 401

  Scenario: Getting me
    Given existing user "alice@example.com" with password "SecurePass123!"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I request me profile
    Then account API status should be 200

  Scenario: Rejecting me without token
    When I request me profile without authorization
    Then account API status should be 401

  Scenario: Updating me
    Given existing user "alice@example.com" with password "SecurePass123!"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I update me email to "alice2@example.com"
    Then account API status should be 200

  Scenario: Rejecting me update to duplicate email
    Given existing user "alice@example.com" with password "SecurePass123!"
    And existing user "bob@example.com" with password "SecurePass123!"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I update me email to "bob@example.com"
    Then account API status should be 409

  Scenario: Deleting me
    Given existing user "alice@example.com" with password "SecurePass123!"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I delete me
    Then account API status should be 204

  Scenario: Signing out current session
    Given existing user "alice@example.com" with password "SecurePass123!"
    And I am signed in with email "alice@example.com" and password "SecurePass123!"
    When I sign out current session
    Then account API status should be 204

  Scenario: Rejecting sign out with invalid token
    When I sign out current session with invalid token
    Then account API status should be 401
