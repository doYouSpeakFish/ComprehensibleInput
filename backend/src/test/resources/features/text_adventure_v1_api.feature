Feature: Text adventure v1 API
  The v1 API should be RESTful, user-scoped, and store text adventure messages as a parent-linked tree.

  Background:
    Given I am authenticated as a user account

  @v1
  Scenario: Creating an adventure returns metadata and the first narrator message
    When I create a text adventure with learning language "es" and translation language "en"
    Then the response status is 201
    And the response includes an adventure id
    And the response includes a title
    And the response includes adventure status "active"
    And the response includes a latest narrator message

  @v1
  Scenario: Listing adventures returns only adventures owned by the current user
    Given user A has an adventure
    And user B has an adventure
    When user A lists adventures
    Then the response status is 200
    And only adventures owned by user A are returned

  @v1
  Scenario: Listing adventures returns all adventures for the user
    Given I have 3 adventures
    When I list adventures
    Then the response status is 200
    And exactly 3 adventures are returned

  @v1
  Scenario: Reading one adventure returns metadata for the owner
    Given I have an existing adventure
    When I fetch that adventure by id
    Then the response status is 200
    And the response includes learning language
    And the response includes translation language
    And the response includes updated timestamp

  @v1
  Scenario: Reading one adventure as a different user returns not found
    Given user A has an adventure
    And I am authenticated as user B
    When I fetch user A adventure by id
    Then the response status is 404

  @v1
  Scenario: Posting a user message structures it via AI and returns formatted sentences
    Given I have an existing adventure with root AI message id "msg_ai_root"
    And the next message id is "msg_user_1"
    When I post a message with type "user", parent id "msg_ai_root", and text "Entro en la torre"
    Then the response status is 201
    And the response message id is "msg_user_1"
    And the response message has type "user"
    And the response message parent id is "msg_ai_root"
    And the response includes generated sentences and translations
    And no AI response has been generated for message id "msg_user_1"

  @v1
  Scenario: Posting a user message fails when AI structuring fails
    Given I have an existing adventure with root AI message id "msg_ai_root"
    And the AI structuring will fail for the next user message
    When I post a message with type "user", parent id "msg_ai_root", and text "Entro en la torre"
    Then the response status is 500

  @v1
  Scenario: Posting an AI message generates a response to a specific user message
    Given I have an existing adventure with stored user message id "msg_user_1" and text "Abro la puerta roja"
    And the next message id is "msg_ai_1"
    When I post a message with type "AI" and parent id "msg_user_1"
    Then the response status is 201
    And the response message id is "msg_ai_1"
    And the response message has type "AI"
    And the response message parent id is "msg_user_1"
    And the response includes generated sentences and translations

  @v1
  Scenario: Retrying AI generation reuses the same parent user message
    Given I have an existing adventure with stored user message id "msg_user_1" and text "Miro detrás de mí"
    And AI generation for message id "msg_user_1" has failed without storing an AI message
    And the next message id is "msg_ai_retry_1"
    When I post a message with type "AI" and parent id "msg_user_1"
    Then the response status is 201
    And the response message id is "msg_ai_retry_1"
    And the response message has type "AI"
    And the response message parent id is "msg_user_1"
    And reading adventure messages returns message id "msg_user_1" followed by message id "msg_ai_retry_1"

  @v1
  Scenario: Adventure messages expose parent ids for branching
    Given I have an existing adventure with root AI message id "msg_ai_root"
    And I have stored user message id "msg_user_left" with parent id "msg_ai_root" and text "Voy a la izquierda"
    And I have stored user message id "msg_user_right" with parent id "msg_ai_root" and text "Voy a la derecha"
    When I fetch adventure messages
    Then the response status is 200
    And every returned message includes an id
    And every non-root returned message includes its parent id
    And message id "msg_user_left" has parent id "msg_ai_root"
    And message id "msg_user_right" has parent id "msg_ai_root"

  @v1
  Scenario: Posting a message with an unknown parent returns not found
    Given I have an existing adventure
    When I post a message with type "user", parent id "msg_unknown", and text "Sigo adelante"
    Then the response status is 404

  @v1
  Scenario: Posting a user message without text is rejected
    Given I have an existing adventure with root AI message id "msg_ai_root"
    When I post a message with type "user", parent id "msg_ai_root", and empty text
    Then the response status is 400

  @v1
  Scenario: Posting an AI message with text is rejected
    Given I have an existing adventure with stored user message id "msg_user_1" and text "Examino la llave"
    When I post a message with type "AI", parent id "msg_user_1", and text "This should not be accepted"
    Then the response status is 400

  @v1
  Scenario: Posting an unsupported message type is rejected
    Given I have an existing adventure with root AI message id "msg_ai_root"
    When I post a message with type "system", parent id "msg_ai_root", and text "debug"
    Then the response status is 400

  @v1
  Scenario: Posting a user message to an unknown adventure returns not found
    When I post a message with type "user" to unknown adventure id "adv_unknown", parent id "msg_root", and text "Sigo adelante"
    Then the response status is 404

  @v1
  Scenario: Posting a user message to another user adventure returns not found
    Given user A has an adventure with root AI message id "msg_ai_root"
    And I am authenticated as user B
    When I post a message with type "user", parent id "msg_ai_root", and text "Sigo adelante"
    Then the response status is 404

  @v1
  Scenario: Reading adventure messages returns parent-linked messages
    Given I have an existing adventure with root AI message id "msg_ai_root"
    And I have stored user message id "msg_user_1" with parent id "msg_ai_root" and text "Abro la puerta"
    And I have stored AI message id "msg_ai_1" with parent id "msg_user_1"
    When I fetch adventure messages
    Then the response status is 200
    And messages are returned with ids and parent ids
    And root message id "msg_ai_root" has no parent id
    And message id "msg_user_1" has parent id "msg_ai_root"
    And message id "msg_ai_1" has parent id "msg_user_1"

  @v1
  Scenario: Reading adventure messages returns all messages in the tree
    Given I have an existing adventure with root AI message id "msg_ai_root"
    And I have stored user message id "msg_user_left" with parent id "msg_ai_root" and text "Voy a la izquierda"
    And I have stored user message id "msg_user_right" with parent id "msg_ai_root" and text "Voy a la derecha"
    And I have stored AI message id "msg_ai_left" with parent id "msg_user_left"
    When I fetch adventure messages
    Then the response status is 200
    And messages with ids "msg_ai_root", "msg_user_left", "msg_user_right", and "msg_ai_left" are returned

  @v1
  Scenario: Reading adventure messages from another user adventure returns not found
    Given user A has an adventure
    And I am authenticated as user B
    When I fetch messages for user A adventure
    Then the response status is 404

  @v1
  Scenario: Deleting one adventure removes all nested messages and sentences
    Given I have an existing adventure with messages
    When I delete that adventure
    Then the response status is 204
    And reading that adventure returns 404
    And reading its messages returns 404

  @v1
  Scenario: Deleting one adventure owned by another user returns not found
    Given user A has an adventure
    And I am authenticated as user B
    When I delete user A adventure
    Then the response status is 404

  @v1
  Scenario: Bulk deleting my adventures removes all my generated content only
    Given user A has 2 adventures
    And user B has 1 adventure
    And I am authenticated as user A
    When I delete all my adventures
    Then the response status is 204
    And user A has no adventures
    And user B adventure still exists

  @v1
  Scenario: Requests without authentication are rejected
    Given I am not authenticated
    When I create a text adventure with learning language "es" and translation language "en"
    Then the response status is 401
    When I list adventures
    Then the response status is 401
    When I fetch adventure messages
    Then the response status is 401

  @v1
  Scenario: Creating adventure with invalid language payload is rejected
    Given I am authenticated as a user account
    When I create a text adventure with invalid language payload
    Then the response status is 400

  @v1
  Scenario: Text adventure requests are rejected once the shared rate limit is exceeded
    Given I am authenticated as a user account
    When I exhaust the text adventure rate limit and make one more request
    Then the response status is 429

  @v1
  Scenario: The text adventure rate limit is shared across all users
    Given I am authenticated as a user account
    When user A exhausts the text adventure rate limit and user B makes a request
    Then the response status is 429
