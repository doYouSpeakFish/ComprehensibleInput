Feature: Text adventures list

  # The text adventures list is offline-first: adventures are read from the local
  # database (scoped to the signed-in user) and refreshed from the v1 backend
  # (GET /v1/adventures). It requires a signed-in account; signed-out users see a
  # prompt to sign in. Swiping a row away deletes the adventure at once
  # (DELETE /v1/adventures/{id}) and offers an undo, which restores it
  # (DELETE /v1/adventures/{id}/deletion).
  # The top bar matches the story list's: an up button, the language picker, and
  # a settings action. The picker reads and writes the global language settings,
  # which decide the languages a new adventure is started in
  # (POST /v1/adventures). The screen title is shown in the content, above the
  # list.

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

  Scenario: The screen title is shown with an up button in the top bar
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    Then the text adventures title is shown
    And the up button is shown

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

  Scenario: Swiping an adventure away deletes it
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    When I swipe to delete the "Lantern Trail" adventure
    Then the "Lantern Trail" adventure is not listed
    And the adventure deleted message is shown

  Scenario: Undoing a deletion restores the adventure
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    When I swipe to delete the "Lantern Trail" adventure
    And I undo the deletion
    Then the "Lantern Trail" adventure is listed

  Scenario: The undo message goes away on its own and the deletion stands
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    When I swipe to delete the "Lantern Trail" adventure
    And the undo message times out
    Then the adventure deleted message is not shown
    And the "Lantern Trail" adventure is not listed

  Scenario: A short swipe does not delete an adventure
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the text adventures screen is open
    When I partially swipe the "Lantern Trail" adventure
    Then the "Lantern Trail" adventure is listed
    And the adventure deleted message is not shown

  Scenario: A deleted adventure is restored when the delete request fails
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the delete adventure request will fail
    And the text adventures screen is open
    When I swipe to delete the "Lantern Trail" adventure
    Then the "Lantern Trail" adventure is listed
    And the adventures error message is shown

  Scenario: An error is shown when undoing a deletion fails
    Given I am signed in as "user@example.com"
    And the adventures request will return the "Lantern Trail" adventure
    And the restore adventure request will fail
    And the text adventures screen is open
    When I swipe to delete the "Lantern Trail" adventure
    And I undo the deletion
    Then the "Lantern Trail" adventure is not listed
    And the adventures error message is shown

  Scenario: Starting a new adventure opens the chat screen
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    When I start a new adventure
    Then the text adventure chat is shown

  # ---------------------------------------------------------------------------
  # The language picker in the top bar
  # ---------------------------------------------------------------------------

  Scenario: The language picker shows the current languages
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    Then the language picker shows German as the learning language
    And the language picker shows English as the translation language

  Scenario: The language picker is shown to signed-out users
    Given I am signed out
    And the text adventures screen is open
    Then the language picker shows German as the learning language
    And the language picker shows English as the translation language

  Scenario: Changing the learning language updates the picker
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    When I set the learning language to Spanish
    Then the language picker shows Spanish as the learning language
    And the language picker shows English as the translation language

  Scenario: Changing the translation language updates the picker
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    When I set the translation language to Spanish
    Then the language picker shows Spanish as the translation language
    And the language picker shows German as the learning language

  Scenario: Choosing the translation language as the learning language swaps them
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    When I set the learning language to English
    Then the language picker shows English as the learning language
    And the language picker shows German as the translation language

  Scenario: Choosing the learning language as the translation language swaps them
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    When I set the translation language to German
    Then the language picker shows German as the translation language
    And the language picker shows English as the learning language

  Scenario: A new adventure is started in the selected languages
    Given I am signed in as "user@example.com"
    And the adventures request will return no adventures
    And the text adventures screen is open
    When I set the learning language to Spanish
    And I start a new adventure
    Then the adventure is started learning Spanish with translations in English
