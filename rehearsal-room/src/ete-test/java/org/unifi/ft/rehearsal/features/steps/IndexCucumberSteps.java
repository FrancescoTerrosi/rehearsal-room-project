package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.unifi.ft.rehearsal.configurations.MongoConfig;
import org.unifi.ft.rehearsal.configurations.WebSecurityConfig;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.bonigarcia.wdm.ChromeDriverManager;

@Import({WebSecurityConfig.class, MongoConfig.class})
@SpringBootTest
public class IndexCucumberSteps {
	
	private static final String HOMEPAGE = "localhost:";
	
	private int port = 11111;
	
	private WebDriver driver;
	
	@BeforeClass
	public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
	}
	
	@Before
	public void setupDriver() {
		driver = new ChromeDriver();
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
		driver.get(HOMEPAGE+port);
		System.out.println(driver.getCurrentUrl());
		assertEquals("Rehearsal Rooms",driver.getTitle());
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
	
}
