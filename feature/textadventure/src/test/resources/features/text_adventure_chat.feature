Feature: Text adventure chat

  # The chat screen drives a conversation with the AI. Starting a new adventure
  # navigates here immediately in a loading state while the first message is
  # generated (POST /v1/adventures). User messages are shown optimistically and
  # become translatable once submitted (POST .../messages type=user). The AI reply
  # is then generated (POST .../messages type=AI). While a message is generating a
  # placeholder cycles through phrases; on failure an error and retry are shown in
  # its place. AI (and submitted user) sentences are tap-to-translate, matching the
  # story reader. The list scrolls to each newly added message.

  # ---------------------------------------------------------------------------
  # Starting a new adventure
  # ---------------------------------------------------------------------------

  Scenario: Starting a new adventure shows the chat in a loading state
    Given I am signed in as "user@example.com"
    And the start adventure request is delayed
    When I start a new adventure
    Then the text adventure chat is shown
    And a generating message placeholder is shown

  Scenario: The placeholder cycles through phrases while generating
    Given I am signed in as "user@example.com"
    And the start adventure request is delayed
    When I start a new adventure
    Then the generating message placeholder cycles through phrases

  Scenario: The first message is shown when the adventure starts
    Given I am signed in as "user@example.com"
    And starting an adventure returns "You arrive at a quiet harbor."
    When I start a new adventure
    Then the generating message placeholder is hidden
    And the text adventure shows "You arrive at a quiet harbor."

  Scenario: An error and retry are shown when the first message fails
    Given I am signed in as "user@example.com"
    And the start adventure request will fail
    When I start a new adventure
    Then the generating message placeholder is hidden
    And the generation error message is shown
    And the retry button is shown

  Scenario: Retrying after a failed start shows the placeholder again
    Given I am signed in as "user@example.com"
    And the start adventure request will fail
    When I start a new adventure
    And the generation error message is shown
    When I tap the retry button
    Then the generation error message is hidden
    And a generating message placeholder is shown

  Scenario: A system busy message and retry are shown when starting an adventure is rate limited
    Given I am signed in as "user@example.com"
    And the start adventure request will be rate limited
    When I start a new adventure
    Then the generating message placeholder is hidden
    And the chat shows a system busy message
    And the retry button is shown

  Scenario: An AI sentence can be translated by tapping it
    Given I am signed in as "user@example.com"
    And starting an adventure returns "You arrive at a quiet harbor." translated as "Llegas a un puerto tranquilo."
    When I start a new adventure
    And I tap "You arrive at a quiet harbor." to translate it
    Then the text adventure shows "Llegas a un puerto tranquilo."

  Scenario: A translated AI sentence can be switched back to the learning language
    Given I am signed in as "user@example.com"
    And starting an adventure returns "You arrive at a quiet harbor." translated as "Llegas a un puerto tranquilo."
    When I start a new adventure
    And I tap "You arrive at a quiet harbor." to translate it
    And I tap "Llegas a un puerto tranquilo." to translate it
    Then the text adventure shows "You arrive at a quiet harbor."

  Scenario: The adventure's cover image is shown at the start of the chat
    Given I am signed in as "user@example.com"
    And starting an adventure returns "You arrive at a quiet harbor." with a cover image
    When I start a new adventure
    Then the text adventure shows "You arrive at a quiet harbor."
    And the adventure cover image is shown

  Scenario: A cached adventure shows its messages immediately
    Given I am signed in as "user@example.com"
    And the "Lantern Trail" adventure is cached with message "A lantern lights the way."
    And messages requests are delayed
    When I open the "Lantern Trail" adventure
    Then the text adventure shows "A lantern lights the way."

  Scenario: The chat title shows the adventure's title
    Given I am signed in as "user@example.com"
    And the "Lantern Trail" adventure is cached with message "A lantern lights the way."
    When I open the "Lantern Trail" adventure
    Then the chat title is "Lantern Trail"

  Scenario: The adventure title can be translated by tapping it
    Given I am signed in as "user@example.com"
    And the "Lantern Trail" adventure, whose title translates to "El Sendero del Farol", is cached with message "A lantern lights the way."
    When I open the "Lantern Trail" adventure
    Then the chat title is "Lantern Trail"
    When I tap the adventure title
    Then the chat title is "El Sendero del Farol"

  Scenario: A translated adventure title can be switched back to the learning language
    Given I am signed in as "user@example.com"
    And the "Lantern Trail" adventure, whose title translates to "El Sendero del Farol", is cached with message "A lantern lights the way."
    When I open the "Lantern Trail" adventure
    And I tap the adventure title
    Then the chat title is "El Sendero del Farol"
    When I tap the adventure title
    Then the chat title is "Lantern Trail"

  Scenario: Opening a cached adventure shows the backend's refreshed conversation
    Given I am signed in as "user@example.com"
    And the "Lantern Trail" adventure is cached with message "A stale cached line."
    And the "Lantern Trail" adventure refreshes to "A fresh line from the server."
    When I open the "Lantern Trail" adventure
    Then the text adventure shows "A fresh line from the server."

  # ---------------------------------------------------------------------------
  # Sending user messages and generating AI responses
  # ---------------------------------------------------------------------------

  Scenario: A user message is shown as soon as it is sent
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the user message request is delayed
    When I send the message "I walk toward the dock."
    Then the text adventure shows "I walk toward the dock."

  Scenario: An optimistic user message stays visible when tapped before it is submitted
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the user message request is delayed
    When I send the message "I walk toward the dock."
    And I tap "I walk toward the dock." to translate it
    Then the text adventure shows "I walk toward the dock."

  Scenario: The input is disabled while a user message is being sent
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the user message request is delayed
    When I send the message "I walk toward the dock."
    Then the text adventure input is disabled

  Scenario: A user message can be translated once it has been submitted
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And submitting "I walk toward the dock." returns the translation "Camino hacia el muelle."
    When I send the message "I walk toward the dock."
    And I tap "I walk toward the dock." to translate it
    Then the text adventure shows "Camino hacia el muelle."

  Scenario: An error and retry are shown when submitting a user message fails
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the user message request will fail
    When I send the message "I walk toward the dock."
    Then the message error is shown
    And the retry button is shown

  Scenario: A system busy message and retry are shown when submitting a user message is rate limited
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the user message request will be rate limited
    When I send the message "I walk toward the dock."
    Then the chat shows a system busy message
    And the retry button is shown

  Scenario: A generating placeholder is shown while the AI responds
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the AI response request is delayed
    When I send the message "I walk toward the dock."
    Then a generating message placeholder is shown

  Scenario: The AI response is shown when generation completes
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the AI responds with "A lantern flickers on the dock."
    When I send the message "I walk toward the dock."
    Then the generating message placeholder is hidden
    And the text adventure shows "A lantern flickers on the dock."

  Scenario: An error and retry are shown when the AI response fails
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the AI response request will fail
    When I send the message "I walk toward the dock."
    Then the generating message placeholder is hidden
    And the generation error message is shown
    And the retry button is shown

  Scenario: Retrying a failed AI response shows the placeholder again
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the AI response request will fail
    When I send the message "I walk toward the dock."
    And the generation error message is shown
    When I tap the retry button
    Then the generation error message is hidden
    And a generating message placeholder is shown

  Scenario: A system busy message and retry are shown when the AI response is rate limited
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the AI response request will be rate limited
    When I send the message "I walk toward the dock."
    Then the generating message placeholder is hidden
    And the chat shows a system busy message
    And the retry button is shown

  Scenario: The chat scrolls to a newly added message
    Given I am signed in as "user@example.com"
    And an adventure has started with a long opening passage
    And the AI responds with "A lantern flickers on the dock."
    When I send the message "I walk toward the dock."
    Then the text adventure shows "A lantern flickers on the dock."

  Scenario: The input is hidden when the adventure ends
    Given I am signed in as "user@example.com"
    And an adventure has started with "You arrive at a quiet harbor."
    And the AI responds with the ending "The fog lifts and the journey ends."
    When I send the message "I take the lantern."
    Then the text adventure shows "The fog lifts and the journey ends."
    And the text adventure input is hidden
