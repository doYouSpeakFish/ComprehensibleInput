Feature: Text adventure v1 API
  The v1 API should be RESTful, user-scoped, and rely on server-side history storage.

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
  Scenario: Posting a player message generates and stores a narrator reply using server-side history
    Given I have an existing adventure
    And the adventure has prior narrator and player turns stored in the database
    When I post a new player message "Entro en la torre" to the adventure messages collection
    Then the response status is 201
    And the response includes both stored player message and generated narrator message
    And the request does not require a full history payload

  @v1
  Scenario: Posting a player message to an ended adventure returns conflict
    Given I have an adventure that is ended
    When I post a new player message "Sigo adelante" to the adventure messages collection
    Then the response status is 409

  @v1
  Scenario: Posting a player message to an unknown adventure returns not found
    When I post a new player message to unknown adventure id "adv_unknown"
    Then the response status is 404

  @v1
  Scenario: Posting a player message to another user adventure returns not found
    Given user A has an adventure
    And I am authenticated as user B
    When I post a new player message to user A adventure
    Then the response status is 404

  @v1
  Scenario: Reading adventure messages returns chronological turns
    Given I have an existing adventure with 5 turns
    When I fetch adventure messages
    Then the response status is 200
    And messages are ordered by turn index ascending

  @v1
  Scenario: Reading adventure messages returns all turns
    Given I have an existing adventure with 5 turns
    When I fetch adventure messages
    Then the response status is 200
    And exactly 5 messages are returned

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
  Scenario: Posting an empty player message is rejected
    Given I have an existing adventure
    When I post an empty player message to the adventure messages collection
    Then the response status is 400
