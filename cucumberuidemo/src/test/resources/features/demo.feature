Feature: Compose UI driven by Cucumber

  Scenario: The demo screen greets the user
    Given the demo screen is displayed
    Then the greeting shows "Hello"

  Scenario: Tapping the button updates the greeting
    Given the demo screen is displayed
    When I tap the button
    Then the greeting shows "Clicked"
