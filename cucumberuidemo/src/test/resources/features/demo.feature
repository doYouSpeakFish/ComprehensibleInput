Feature: Compose UI driven by Cucumber

  Scenario: Tapping the button updates the greeting
    Given the demo screen is displayed
    Then the greeting shows "Hello"
    When I tap the button
    Then the greeting shows "Clicked"

  Scenario: The greeting starts as Hello
    Given the demo screen is displayed
    Then the greeting shows "Hello"
