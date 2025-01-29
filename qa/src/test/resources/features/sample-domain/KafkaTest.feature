Feature: Kafka test feature

  Scenario: Kafka - Successfully send exchange rates data
    When the following rates data is prepared:
     | pair      | baseCurrency | quoteCurrency | ask    | bid   |
     | USD/JPY   | USD          | JPY           | *      | *     |
     | EUR/USD   | EUR          | USD           | *      | *     |
     | PLN/USD   | PLN          | USD           | *      | *     |

    When the rates are sent by Bloomberg
    Then FX Rates landed on kafka
    Then Rates successfully updated

