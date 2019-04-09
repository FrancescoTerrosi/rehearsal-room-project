package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.web.RegisterPageWebController;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(loader = SpringBootContextLoader.class)
public class RegisterPageCucumberSteps {

	private static final String HOMEPAGE = "http://localhost:";

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@Autowired
	private IBandDetailsMongoRepository repository;

	@Before
	public void setupDriver() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-gpu");
		driver = new ChromeDriver(chromeOptions);
		repository.deleteAll();
	}

	@After
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
		repository.deleteAll();
	}

	@Given("^The DB is running with no user$")
	public void the_DB_is_running() throws Throwable {
		assertNotNull(repository);
		assertEquals(0, repository.count());
	}

	@Given("^The User request the /register url$")
	public void the_User_request_the_register_url() throws Throwable {
		driver.get(HOMEPAGE + port + "/register");
		assertEquals(HOMEPAGE + port + "/register", driver.getCurrentUrl());
		assertEquals("Registration Page", driver.getTitle());
	}

	@When("^The user register for a username that does not exist$")
	public void the_user_register_for_a_username_that_does_not_exist() throws Throwable {
		driver.findElement(By.id("username")).sendKeys("NewUser");
	}

	@When("^The user gives a password$")
	public void the_user_gives_a_password() throws Throwable {
		driver.findElement(By.id("password")).sendKeys("NewPassword");
	}

	@When("^The user confirm that password$")
	public void the_user_confirm_that_password() throws Throwable {
		driver.findElement(By.id("confirmPassword")).sendKeys("NewPassword");
	}

	@When("^The user clicks on the register button$")
	public void the_user_clicks_on_the_register_button() throws Throwable {
		driver.findElement(By.id("submit")).click();
	}

	@Then("^The user is registered in the DB$")
	public void the_user_is_registered_in_the_DB() throws Throwable {
		assertEquals(HOMEPAGE + port + "/", driver.getCurrentUrl());
		assertEquals(1, repository.count());
		List<BandDetails> list = repository.findAll();
		assertEquals(1, list.size());
		assertEquals("NewUser", list.get(0).getUsername());
	}

	@Given("^The DB is running with some user in it$")
	public void theDBIsRunningWithSomeUserInIt() throws Throwable {
		BandDetails temp = new BandDetails("SomeBand", "SomePw", "USER");
		repository.save(temp);
		assertEquals(1, repository.count());
	}

	@When("^The user register for a username that already exists$")
	public void theUserRegisterForAUsernameThatAlreadyExists() throws Throwable {
		driver.findElement(By.id("username")).sendKeys("SomeBand");
	}

	@Then("^The user registration is rejected and the db has the same elements as before$")
	public void the_user_registration_is_rejected_and_the_db_has_the_same_elements_as_before() throws Throwable {
		assertEquals(1, repository.count());
	}

	@Then("^An error message is displayed$")
	public void an_error_message_is_displayed() throws Throwable {
		WebElement error = driver.findElement(By.id("error"));
		assertEquals(RegisterPageWebController.REGISTRATION_USERNAME_ERROR, error.getText());
	}

	@When("^The user register leaving the username field empty$")
	public void theUserRegisterLeavingTheUsernameFieldEmpty() throws Throwable {
		driver.findElement(By.id("username")).sendKeys("");
	}

	@Then("^The user registration is rejected and the db still has zero users$")
	public void the_user_registration_is_rejected_and_the_db_still_has_zero_users() throws Throwable {
		assertEquals(0, repository.count());
	}

	@Then("^An Empty Field error message is displayed$")
	public void an_Empty_Field_error_message_is_displayed() throws Throwable {
		WebElement error = driver.findElement(By.id("error"));
		assertEquals(RegisterPageWebController.EMPTY_FIELDS_ERROR, error.getText());
	}

	@When("^The user gives a wrong confirmPassword$")
	public void the_user_gives_a_wrong_confirmPassword() throws Throwable {
		driver.findElement(By.id("confirmPassword")).sendKeys("wrongPw");
	}

	@Then("^A password error message is displayed$")
	public void a_password_error_message_is_displayed() throws Throwable {
		WebElement error = driver.findElement(By.id("error"));
		assertEquals(RegisterPageWebController.REGISTRATION_PASSW_ERROR, error.getText());
	}

}
