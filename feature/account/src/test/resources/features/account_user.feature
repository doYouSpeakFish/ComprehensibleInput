Feature: Account user record

  # When a user signs in, the account module upserts a local user record (a Room
  # entity that lives in :common) keyed by the backend account id from the sign-in
  # response. Adventures reference this record by foreign key with cascade-on-delete,
  # so the record is removed when the account is deleted. The account module also
  # exposes the current user reactively as a flow.

  Scenario: Signing in creates a local user record
    Given the account screen is open
    And the sign in request will succeed with user id "user-1"
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then a local user record exists with id "user-1"

  # Authentication must not depend on the local cache write: if persisting the user
  # record fails, the user is still signed in rather than being bounced back out.
  Scenario: Signing in succeeds even when the local user record cannot be saved
    Given the account screen is open
    And the sign in request will succeed with user id "user-1"
    And saving the local user record will fail
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the signed in email "user@example.com" is shown

  Scenario: The current user is exposed after signing in
    Given the account screen is open
    And the sign in request will succeed with user id "user-1"
    When I enter the sign in email "user@example.com"
    And I enter the sign in password "password12345"
    And I submit the sign in form
    Then the current user id is "user-1"

  Scenario: No user is exposed when signed out
    Given the account screen is open
    Then there is no current user

  Scenario: Signing out keeps the local user record for offline data
    Given I am signed in as "user@example.com" with id "user-1"
    And the account screen is open
    When I tap the sign out button
    Then a local user record exists with id "user-1"

  Scenario: Deleting the account removes the local user record
    Given I am signed in as "user@example.com" with id "user-1"
    And the delete account screen is open
    And the delete account request will succeed
    When I enter the delete account password "password12345"
    And I submit the delete account form
    Then no local user record exists with id "user-1"
