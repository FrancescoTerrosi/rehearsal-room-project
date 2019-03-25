@tag
Feature: Schedule Queries and Operations

  @tag1
  Scenario: User want to list schedules by date
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And There are some schedules in the DB
    When The user requests the /schedule url
    And The user requests the list of saved schedules by date
    And There are schedules saved on that day
    Then A list of saved schedules is shown
    
  @tag2
  Scenario: User want to list schedules by date but no schedules on that day
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And There are some schedules in the DB
    When The user requests the /schedule url
    And The user requests the list of saved schedules by date on a free day
    And There are not schedules saved on that day
    Then A proper message is shown to the user
    
  @tag3
  Scenario: User want to list schedules by date but leaves a blank field
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And There are some schedules in the DB
    When The user requests the /schedule url
    And The user requests the list of saved schedules by date but leaves a blank field
    Then An invalid date message is shown
    
  @tag4
  Scenario: User want to list schedules by date using illegal charachters
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And There are some schedules in the DB
    When The user requests the /schedule url
    And The user requests the list of saved schedules by date but using illegal charachters
    Then An invalid date message is shown
    
  @tag5
  Scenario: User want to list schedules by name
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And The user scheduled for rehearsals
    When The user requests the /schedule url
    And The user requests the list of saved schedules by name
    Then His list of saved schedules is shown  
  
  @tag6
  Scenario: User want to list schedules by name and delete one
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And The user scheduled for rehearsals
    When The user requests the /schedule url
    And The user requests the list of saved schedules by name
    And The user click the delete button
    Then The schedule is removed from the db
    
  @tag7
  Scenario: User wants to list schedules by room
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    And There are some schedules in the DB
    When The user requests the /schedule url
    And The user requests the list of saved schedules by room
    Then A list of saved schedules is shown
    
  @tag8
  Scenario: User wants to list schedules by room
    Given The server is running
    And The Schedule DB is running
    And The user is registered into the system
    And The user is logged into the system
    When The user requests the /schedule url
    And The user requests the list of saved schedules by room for a free room
    Then A proper message is shown to the user
