Feature: Account user record

  # When a user signs in, the account module upserts a local user record (a Room
  # entity that lives in :common) keyed by the backend account id from GET /v1/me.
  # Adventures reference this record by foreign key with cascade-on-delete, so the
  # record is removed when the account is deleted. The account module also exposes
  # the current user and session token reactively as flows.
  #
  # Increment 2: this whole feature.
  # All scenarios are @ignore-d until increment 2 is implemented.

  @ignore @increment2
  Scenario: Signing in creates a local user record
    Given the account screen is open
    And the sign in request will succeed for user id "user-1" email "user@example.com"
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then a local user record exists with id "user-1"

  @ignore @increment2
  Scenario: The current user is exposed after signing in
    Given the account screen is open
    And the sign in request will succeed for user id "user-1" email "user@example.com"
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the current user id is "user-1"

  @ignore @increment2
  Scenario: No user is exposed when signed out
    Given the account screen is open
    Then there is no current user

  @ignore @increment2
  Scenario: Signing out keeps the local user record for offline data
    Given I am signed in as "user@example.com" with id "user-1"
    And the account screen is open
    When I tap the sign out button
    Then a local user record exists with id "user-1"

  @ignore @increment2
  Scenario: Deleting the account removes the local user record
    Given I am signed in as "user@example.com" with id "user-1"
    And the account screen is open
    And the delete account request will succeed
    When I delete the account with password "password12345"
    Then no local user record exists with id "user-1"
