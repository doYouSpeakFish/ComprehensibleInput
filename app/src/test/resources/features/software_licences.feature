Feature: Software licences

  Scenario: The software licences screen lists its licences
    Given the software licences screen is open
    Then the software licences title is shown

  Scenario: The AboutLibraries licence appears in the list
    Given the software licences screen is open
    Then the "AboutLibraries Core Library" licence is shown
