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


  Scenario: Kafka - Negative - empty fields
    When the following rates data is prepared:
      | pair      | baseCurrency | quoteCurrency | ask    | bid   |
      | USD/JPY   | USD          | -             | *      | *     |
      | EUR/USD   | -            | -             | *      | *     |
      | PLN/USD   | -            | -             | *      | *     |
    When the rates are sent by Bloomberg
    Then FX Rates NOT landed on kafka
    When the following rates data is prepared:
      | pair      | baseCurrency | quoteCurrency | ask    | bid   |
      |           |              |               |        |       |
    When the rates are sent by Bloomberg
    Then FX Rates NOT landed on kafka
    When the following rates data is prepared:
      | pair      | baseCurrency          | quoteCurrency     | ask            | bid        |
      |           |     null              |    null           |  null          |            |
    When the rates are sent by Bloomberg
    Then FX Rates NOT landed on kafka
