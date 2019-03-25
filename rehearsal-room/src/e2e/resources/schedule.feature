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
    And The schedule is not stored in the empty DB
    
  @tag3
  Scenario: User create a schedule with blank fields
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    When The user requests the /schedule url
    And The user schedule for a free room but leaving a blank field
    Then An invalid date message is shown
    And The schedule is not stored in the empty DB
    
  @tag4
  Scenario: User create a schedule for a room that is not free
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And There are some schedules in the DB
    When The user requests the /schedule url
    And The user schedule for a room that is not free
    Then A room not free message is shown
    And The schedule is not stored in the DB
    
  @tag5
  Scenario: User create a schedule for a room before 5 minutes from now
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    When The user requests the /schedule url
    And The user schedule for a room in a time t < (now + 5 minutes)
    Then A funny message is shown
    And The schedule is not stored in the empty DB
    
  @tag6
  Scenario: User create a schedule using illegal charachters
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    When The user requests the /schedule url
    And The user schedule for a free room but using illegal charachters
    Then An invalid date message is shown
    And The schedule is not stored in the empty DB
    
  @tag7
  Scenario: User logs out
  	Given The server is running
  	And The user is registered into the system
    And The user is logged into the system
    When The user requests the /schedule url
    And The user clicks on the Logout button
    Then The user is redirected to the homepage
    And The user can not visit /schedule page
    
  
