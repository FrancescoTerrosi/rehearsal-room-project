#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@tag
Feature: Registration page

  @tag1
  Scenario: User Registration
    Given The DB is running with no user
    And The User request the /register url
    When The user register for a username that does not exist
    And The user gives a password
    And The user confirm that password
    And The user click on the register button
    Then The user is registered in the DB
    
	@tag2
	Scenario: User Registration but a user with the same name exists
		Given The DB is running with some user in it
		And The User request the /register url
		When The user register for a username that already exists
    And The user gives a password
    And The user confirm that password
    And The user click on the register button
    Then The user registration is rejected and the db has the same elements as before
    And An error message is displayed

  @tag3
  Scenario: User Registration but the user leave an empty field
  	Given The DB is running with no user
  	And The User request the /register url
  	When The user register leaving the username field empty
  	And The user gives a password
  	And The user confirm that password
    And The user click on the register button
  	Then The user registration is rejected and the db still has zero users
  	And An Empty Field error message is displayed
  	
	@tag4
	Scenario: User Registration but the user mistakes password
		Given The DB is running with no user
		And The User request the /register url
		When The user register for a username that does not exist
  	And The user gives a password
  	And The user gives a wrong confirmPassword
    And The user click on the register button
  	Then The user registration is rejected and the db still has zero users
  	And A password error message is displayed
  