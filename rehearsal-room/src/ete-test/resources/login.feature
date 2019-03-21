@tag
Feature: Login Page

  @tag1
  Scenario: User wants to log in
    Given The server is running
    And The DB is running
    And The User registered in the system
    When The user requests the /login url
    And The user provides a username
    And The user provides the password associated to that username
    And The user press the Login button
    Then The user is logged in the system
    And The user is redirected to the Schedule page
