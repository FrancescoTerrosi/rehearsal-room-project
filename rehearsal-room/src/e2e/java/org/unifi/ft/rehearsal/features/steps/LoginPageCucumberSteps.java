package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.web.LoginPageWebController;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(loader = SpringBootContextLoader.class)
public class LoginPageCucumberSteps {

	private static final String HOMEPAGE = "http://localhost:";

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@Autowired
	private IBandDetailsMongoRepository repository;

	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Before
	public void setupDriver() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-gpu");
		driver = new ChromeDriver(chromeOptions);
	}

	@After
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
		repository.deleteAll();
	}

	@Given("^The DB is running with this user saved in$")
	public void the_DB_is_running_with_this_user_saved_in() throws Throwable {
		BandDetails b = new BandDetails("Username", encoder.encode("UserPassword"), "USER");
		assertNotNull(repository);
		repository.save(b);
		assertEquals(1, repository.count());
		assertEquals("Username", repository.findAll().get(0).getUsername());
	}

	@When("^The user requests the /login url$")
	public void the_user_requests_the_login_url() throws Throwable {
		driver.get(HOMEPAGE + port + "/login");
		assertEquals(HOMEPAGE + port + "/login", driver.getCurrentUrl());
		assertEquals("Login Page", driver.getTitle());
	}

	@When("^The user provides a username$")
	public void the_user_provides_a_username() throws Throwable {
		driver.findElement(By.id("username")).sendKeys("Username");
	}

	@When("^The user provides the password associated to that username$")
	public void the_user_provides_the_password_associated_to_that_username() throws Throwable {
		driver.findElement(By.id("password")).sendKeys("UserPassword");
	}

	@When("^The user clicks the login button$")
	public void the_user_clicks_the_login_button() throws Throwable {
		driver.findElement(By.id("submit")).click();
	}

	@Then("^The user is redirected to the Schedule page$")
	public void the_user_is_redirected_to_the_Schedule_page() throws Throwable {
		assertEquals(HOMEPAGE + port + "/schedule", driver.getCurrentUrl());
	}

	@When("^The user provides a wrong username$")
	public void the_user_provides_a_wrong_username() throws Throwable {
		driver.findElement(By.id("username")).sendKeys("WrongName");
	}

	@When("^The user provides a password$")
	public void the_user_provides_a_password() throws Throwable {
		driver.findElement(By.id("password")).sendKeys("UserPassword");
	}

	@When("^The user provides a wrong password$")
	public void the_user_provides_a_wrong_password() throws Throwable {
		driver.findElement(By.id("password")).sendKeys("WrongPassword");
	}

	@Then("^A generic wrong user/password error is displayed$")
	public void a_generic_wrong_user_password_error_is_displayed() throws Throwable {
		WebElement error = driver.findElement(By.id("error"));
		assertEquals(LoginPageWebController.INVALID_USERNAME_OR_PASSW, error.getText());
	}

}
