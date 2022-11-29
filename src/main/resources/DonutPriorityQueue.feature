Feature: Jim wants to create webservices for his online Donut retailer

  Scenario Outline: To Register and generate customer id for customer
    Given the base url "http://jim-donut.com/api/register" to place Donut order
    When customer register Donut site with "<Name>" and "<mailID>"
    Then Generated customer ID sent to his "<mailID>"

    Examples:
      | Name                |                 mailID          |
      |   Test Automation   |   test.automation@testmail.com  |

  Scenario Outline: To fetch the number of orders placed
    Given the base url "http://jim-donut.com/api/orders" to place Donut order
    When customer with "<customerID>" choose Donut "<product>" and "<quantity>"
    Then Jim collects the number of orders

    Examples:
    | customerID | product | quantity |
    |    999     | choco   |    3     |

  Scenario Outline: Customer wants to cancel the existing Donut order
    Given the base url "http://jim-donut.com/api/order-view" to place Donut order
    When customer with "<customerID>" choose Donut "<product>" and "<quantity>"
    Then customer wants to cancel existing order
    And check if the customer ID removed from order list

    Examples:
      | customerID | product  | quantity |
      |     555    |  vanilla |    10    |

  Scenario Outline: Customer wants to delete account
    Given the base url "http://jim-donut.com/api/login" to place Donut order
    When customer with "<customerID>" wants to delete his account
    Then remove customer details from database

    Examples:
      | customerID |
      |    1000    |

    #Negative Scenarios

  Scenario Outline: Try to retrieve order for an unknown customer
    Given the base url "http://jim-donut.com/api/orders" to place Donut order
    When jim tries to fetch invalid "<customerID>" order

    Examples:
      | customerID |
      |     744    |
