Feature: Kafka test feature

  Scenario: Kafka - Successfully send exchange rates data

    Given the API endpoint is "http://localhost:3080/emitEvent"
    And the following rates data is prepared:
      | pair      | baseCurrency | quoteCurrency | ask    | bid   |
      | USD/JPY   | USD          | JPY           | 777.7  | 777.7 |
      | EUR/USD   | EUR          | USD           | 4.777  | 1.77  |
      | PLN/USD   | EUR          | USD           | 4.777  | 1.77  |

    When the rates data is sent to the API
    And the response status code should be 200
    Then message in kafka verified
