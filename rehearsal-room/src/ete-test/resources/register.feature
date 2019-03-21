@tag
Feature: Registration page

  @tag1
  Scenario: User Registration
  	Given The server is running
    And The DB is running with no user
    And The User request the /register url
    When The user register for a username that does not exist
    And The user gives a password
    And The user confirm that password
    And The user clicks on the register button
    Then The user is registered in the DB
    
	@tag2
	Scenario: User Registration but a user with the same name exists
  	Given The server is running
		And The DB is running with some user in it
		And The User request the /register url
		When The user register for a username that already exists
    And The user gives a password
    And The user confirm that password
    And The user clicks on the register button
    Then The user registration is rejected and the db has the same elements as before
    And An error message is displayed

  @tag3
  Scenario: User Registration but the user leave an empty field
  	Given The server is running
  	And The DB is running with no user
  	And The User request the /register url
  	When The user register leaving the username field empty
  	And The user gives a password
  	And The user confirm that password
    And The user clicks on the register button
  	Then The user registration is rejected and the db still has zero users
  	And An Empty Field error message is displayed
  	
	@tag4
	Scenario: User Registration but the user mistakes password
  	Given The server is running
		And The DB is running with no user
		And The User request the /register url
		When The user register for a username that does not exist
  	And The user gives a password
  	And The user gives a wrong confirmPassword
    And The user clicks on the register button
  	Then The user registration is rejected and the db still has zero users
  	And A password error message is displayed
  