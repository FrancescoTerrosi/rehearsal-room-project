@tag
Feature: Login Page

  @tag1
  Scenario: User wants to log in
    Given The server is running
    And The DB is running with this user saved in
    When The user requests the /login url
    And The user provides a username
    And The user provides the password associated to that username
    And The user clicks the login button
    Then The user is redirected to the Schedule page

	@tag2
	Scenario: User want to log in but user does not exist
		Given The server is running
		And The DB is running with this user saved in
		When The user requests the /login url
		And The user provides a wrong username
		And The user provides a password
		And The user clicks the login button
		Then A generic wrong user/password error is displayed
		
	@tag2
	Scenario: User want to log in but the password is wrong
		Given The server is running
		And The DB is running with this user saved in
		When The user requests the /login url
		And The user provides a wrong username
		And The user provides a wrong password
		And The user clicks the login button
		Then A generic wrong user/password error is displayed