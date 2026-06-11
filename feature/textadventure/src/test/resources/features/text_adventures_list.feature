Feature: Text adventures list

  # The text adventures list is offline-first: adventures are read from the local
  # database (scoped to the signed-in user) and refreshed from the v1 backend
  # (GET /v1/adventures). It requires a signed-in account; signed-out users see a
  # prompt to sign in. Adventures can be deleted (DELETE /v1/adventures/{id}).

  Scenario: Signed-out users are prompted to sign in
    Given I am signed out
    And the text adventures screen is open
    Then the text adventures sign in prompt is shown
    And the adventures list is hidden

  Scenario: The sign in prompt opens the account screen
    Given I am signed out
    And the text adventures screen is open
    When I tap the text adventures sign in button
    Then the account screen is shown

  Scenario: A signed-in user with no adventures sees the empty state
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    Then the empty adventures message is shown

  Scenario: The free early access notice is shown on the text adventures screen
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    Then the free early access notice is shown

  Scenario: A signed-in user sees their adventures
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    Then the "Lantern Trail" adventure is listed

  Scenario: A cover image is shown for an adventure that has one
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure with a cover image
    And the text adventures screen is open
    Then the "Lantern Trail" adventure is listed
    And the "Lantern Trail" adventure cover image is shown

  Scenario: No cover image is shown for an adventure without one
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    Then the "Lantern Trail" adventure is listed
    And no cover image is shown for the "Lantern Trail" adventure

  Scenario: A loading indicator is shown while adventures load
    Given I am signed in as "user@example.com"
    And adventures requests are delayed
    And the text adventures screen is open
    Then the adventures loading indicator is shown

  Scenario: Cached adventures are shown before the refresh completes
    Given I am signed in as "user@example.com"
    And the "Lantern Trail" adventure is cached for "user@example.com"
    And adventures requests are delayed
    And the text adventures screen is open
    Then the "Lantern Trail" adventure is listed

  Scenario: An error is shown when adventures cannot be loaded and none are cached
    Given I am signed in as "user@example.com"
    And the adventures request will fail
    And the text adventures screen is open
    Then the adventures error message is shown

  Scenario: A system busy message is shown when the adventures request is rate limited
    Given I am signed in as "user@example.com"
    And the adventures request will be rate limited
    And the text adventures screen is open
    Then the system busy message is shown

  Scenario: Cached adventures stay visible when the refresh fails
    Given I am signed in as "user@example.com"
    And the "Lantern Trail" adventure is cached for "user@example.com"
    And the adventures request will fail
    And the text adventures screen is open
    Then the "Lantern Trail" adventure is listed

  Scenario: A user only sees their own adventures
    Given I am signed in as "user@example.com"
    And the "Forest Echoes" adventure is cached for "other@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    Then the "Lantern Trail" adventure is listed
    And the "Forest Echoes" adventure is not listed

  Scenario: An adventure can be deleted
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    When I delete the "Lantern Trail" adventure
    Then the "Lantern Trail" adventure is not listed

  Scenario: A short swipe does not delete an adventure
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    When I partially swipe the "Lantern Trail" adventure
    Then the "Lantern Trail" adventure is listed

  Scenario: A deleted adventure is restored when the delete request fails
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the delete adventure request will fail
    And the text adventures screen is open
    When I delete the "Lantern Trail" adventure
    Then the "Lantern Trail" adventure is listed
    And the adventures error message is shown

  Scenario: Starting a new adventure opens the chat screen
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    When I start a new adventure
    Then the text adventure chat is shown
