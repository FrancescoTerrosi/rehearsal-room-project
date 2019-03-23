@tag
Feature: Scheduling

  @tag1
  Scenario: User create a schedule
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    When The user requests the /schedule url
    And The user schedule for a free room in valid date
    Then The request is accepted
    And The schedule is stored in the DB
  
  @tag2
  Scenario: User create a schedule for a day that doesn't exist
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    When The user requests the /schedule url
    And The user schedule for a free room in a day the does not exist
    Then An invalid date message is shown
    And The schedule is not stored in the DB
