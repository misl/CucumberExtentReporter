@featureTag
Feature: My First Feature

  @scenarioTag
  Scenario Outline: My First Scenario
    Given I have <test> cukes in my belly
    Then I print

    Examples:
    | test |
    | 1    |
    | 2    |

  Scenario: My Second Scenario
    Given I have 7 cukes in my bellies
    Then I print