package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(loader = SpringBootContextLoader.class)
public class IndexCucumberSteps {

	private static final String HOMEPAGE = "http://localhost:";

	@LocalServerPort
	private int port;

	private WebDriver driver;

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
	}

	@Given("^The server is running$")
	public void the_server_is_running() throws Throwable {
		/*
		 * The server is always running before tests
		 */
	}

	@When("^The user connects to the homepage$")
	public void theUserConnectsToTheHomepage() throws Throwable {
		driver.get(HOMEPAGE + port);
		assertEquals(HOMEPAGE + port + "/", driver.getCurrentUrl());
		assertEquals("Rehearsal Rooms", driver.getTitle());
	}

	@Then("^The homepage with two buttons: link and register, is displayed$")
	public void the_homepage_with_two_buttons_link_and_register_is_displayed() throws Throwable {
		WebElement loginButton = driver.findElement(By.id("login"));
		assertEquals("Login", loginButton.getText());
		WebElement registerButton = driver.findElement(By.id("register"));
		assertEquals("Register", registerButton.getText());

		assertTrue(loginButton.isDisplayed());
		assertTrue(loginButton.isEnabled());

		assertTrue(registerButton.isDisplayed());
		assertTrue(registerButton.isEnabled());
	}

	@Then("^Navbar is displayed$")
	public void navbar_is_displayed() throws Throwable {
		WebElement navBar = driver.findElement(By.id("navbar"));
		assertEquals("Home\nLogin\nRegister", navBar.getText());
	}

}
