package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.unifi.ft.rehearsal.configurations.MongoConfig;
import org.unifi.ft.rehearsal.configurations.WebSecurityConfig;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.services.BandService;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.bonigarcia.wdm.ChromeDriverManager;

@SpringBootTest
public class RegisterPageCucumberSteps {
	
	private static final String HOMEPAGE = "http://localhost:";
	
	private int port = 11111;
	
	private WebDriver driver;
	
	@Autowired
	private IBandDetailsMongoRepository repository;
	
	@BeforeClass
	public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
	}
	
	@Before
	public void setupDriver() {
		repository.deleteAll();
		driver = new ChromeDriver();
	}

	@After
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
		repository.deleteAll();
	}
	
	@Given("^The DB is running$")
	public void the_DB_is_running() throws Throwable {
	    /*
	     * DB is started by server
	     */
	}
	
	@Given("^The User request the /register url$")
	public void the_User_request_the_register_url() throws Throwable {
	    driver.get(HOMEPAGE+port+"/register");
	    assertEquals(HOMEPAGE+port+"/register",driver.getCurrentUrl());
	    assertEquals("Registration Page",driver.getTitle());
	}
	
	@When("^The user register for a username that does not exists$")
	public void the_user_register_for_a_username_that_does_not_exists() throws Throwable {
	    driver.findElement(By.id("username")).sendKeys("NewUser");
	}

	@When("^The user gives a password$")
	public void the_user_gives_a_password() throws Throwable {
	    driver.findElement(By.id("password")).sendKeys("NewPassword");
	}

	@When("^The user confirm that password$")
	public void the_user_confirm_that_password() throws Throwable {
	    driver.findElement(By.id("confirmPassword")).sendKeys("NewPassword");
	    driver.findElement(By.id("submit")).click();
	    System.out.println(driver.getCurrentUrl());
	}	
	
	@Then("^The user is registered in the DB$")
	public void the_user_is_registered_in_the_DB() throws Throwable {
		assertEquals(1, repository.count());
	}
}
