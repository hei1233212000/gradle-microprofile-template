Feature: Find JsonModel

  Scenario: Find JsonModel
    When I query JsonModel and I should see:
      | id           | 1         |
      | custom-field | any value |
