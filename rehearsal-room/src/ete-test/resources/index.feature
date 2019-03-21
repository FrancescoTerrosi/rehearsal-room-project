@tag
Feature: Index Page

  @tag1
  Scenario: GetIndexDisplayLoginRegisterButton
    Given The server is running
    When The user connects to the homepage
    Then The homepage with two buttons: link and register, is displayed
    And Navbar is displayed
