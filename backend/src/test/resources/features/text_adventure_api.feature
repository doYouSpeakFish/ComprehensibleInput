Feature: Text adventure API
  Verify backend endpoint contracts for health, adventure flow,
  and authorization responses used by app integrations

  Scenario: Checking service health
    When I check the health endpoint
    Then the response status should be 200

  Scenario: Starting a text adventure
    Given the AI will return an opening adventure titled "Lantern Trail" with sentence "You wake up." and translation "Despiertas."
    When I start a text adventure in "English" with translations in "Spanish"
    Then the adventure response contains title "Lantern Trail" with sentence "You wake up." and translation "Despiertas."

  Scenario: Continuing a text adventure
    Given the AI will return an opening adventure titled "Lantern Trail" with sentence "You wake up." and translation "Despiertas."
    And the AI will return a continuation for title "Lantern Trail" with sentence "You walk north." and translation "Caminas al norte." that is ending "true"
    When I start a text adventure in "English" with translations in "Spanish"
    And I continue the started adventure with user message "Go north"
    Then the continuation response contains sentence "You walk north." and translation "Caminas al norte." with ending "true"

  Scenario: Reading messages for an existing text adventure
    Given the AI will return an opening adventure titled "Lantern Trail" with sentence "You wake up." and translation "Despiertas."
    And the AI will return a continuation for title "Lantern Trail" with sentence "You walk north." and translation "Caminas al norte." that is ending "true"
    When I start a text adventure in "English" with translations in "Spanish"
    And I continue the started adventure with user message "Go north"
    And I request messages for the started adventure
    Then the messages response contains 2 AI messages

  Scenario: Reading messages includes the generated story content
    Given the AI will return an opening adventure titled "Lantern Trail" with sentence "You wake up." and translation "Despiertas."
    And the AI will return a continuation for title "Lantern Trail" with sentence "You walk north." and translation "Caminas al norte." that is ending "true"
    When I start a text adventure in "English" with translations in "Spanish"
    And I continue the started adventure with user message "Go north"
    And I request messages for the started adventure
    Then the messages response keeps "You wake up." before "You walk north."

  Scenario: Starting a text adventure creates a private internal plan that is not exposed by API
    Given the AI will return an opening adventure titled "Lantern Trail" with sentence "You wake up." and translation "Despiertas." and updated plan "Act 1: Escape the cave."
    When I start a text adventure in "English" with translations in "Spanish"
    Then the adventure response contains title "Lantern Trail" with sentence "You wake up." and translation "Despiertas."
    And the latest adventure response body does not contain "updatedPlan"
    And the stored adventure plan for the started adventure is "Act 1: Escape the cave."

  Scenario: Continuing a text adventure sends the stored plan to the AI and updates it when provided
    Given the AI will return an opening adventure titled "Lantern Trail" with sentence "You wake up." and translation "Despiertas." and updated plan "Act 1: Escape the cave."
    And the AI will return a continuation for title "Lantern Trail" with sentence "You walk north." and translation "Caminas al norte." that is ending "false" and updated plan "Act 2: Find the lantern."
    When I start a text adventure in "English" with translations in "Spanish"
    And I continue the started adventure with user message "Go north"
    Then the continuation response contains sentence "You walk north." and translation "Caminas al norte." with ending "false"
    And the AI continuation request includes current plan "Act 1: Escape the cave."
    And the latest adventure response body does not contain "updatedPlan"
    And the stored adventure plan for the started adventure is "Act 2: Find the lantern."

  Scenario: Continuing a text adventure keeps the stored plan when no updated plan is provided
    Given the AI will return an opening adventure titled "Lantern Trail" with sentence "You wake up." and translation "Despiertas." and updated plan "Act 1: Escape the cave."
    And the AI will return a continuation for title "Lantern Trail" with sentence "You wait." and translation "Esperas." that is ending "false" and no updated plan
    When I start a text adventure in "English" with translations in "Spanish"
    And I continue the started adventure with user message "Wait"
    Then the continuation response contains sentence "You wait." and translation "Esperas." with ending "false"
    And the stored adventure plan for the started adventure is "Act 1: Escape the cave."

  Scenario: Rejecting a request with an invalid API key
    When I start a text adventure with an invalid API key
    Then the response status should be 401

  Scenario: Rejecting a continuation request with an invalid API key
    When I continue an adventure with an invalid API key
    Then the response status should be 401

  Scenario: Rejecting a messages request with an invalid API key
    When I request messages with an invalid API key
    Then the response status should be 401

  Scenario: Returning not found for an unknown adventure id
    When I request messages for adventure id "missing"
    Then the response status should be 404
