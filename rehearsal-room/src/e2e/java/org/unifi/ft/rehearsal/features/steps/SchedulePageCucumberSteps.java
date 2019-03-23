package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.bonigarcia.wdm.ChromeDriverManager;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(loader = SpringBootContextLoader.class)
public class SchedulePageCucumberSteps {

	private static final String HOMEPAGE = "http://localhost:";

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@Autowired
	private IBandDetailsMongoRepository userRepo;

	@Autowired
	private IBandDetailsMongoRepository scheduleRepo;

	@BeforeClass
	public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
	}

	@Before
	public void setupDriver() {
		final ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-gpu");
		driver = new ChromeDriver(chromeOptions);
		scheduleRepo.deleteAll();
	}

	@After
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
		userRepo.deleteAll();
		scheduleRepo.deleteAll();
	}

	@Given("^The Schedule DB is running$")
	public void the_Schedule_DB_is_running() throws Throwable {
		assertNotNull(scheduleRepo);
	}

	@WithMockUser("username")
	@When("^The user requests the /schedule url$")
	public void the_user_requests_the_schedule_url() throws Throwable {
		driver.get(HOMEPAGE+port+"/schedule");
		System.out.println(driver.getCurrentUrl());
	}

	@When("^The user schedule for a free room in valid date$")
	public void the_user_schedule_for_a_free_room_in_valid_date() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}

	@Then("^The request is accepted$")
	public void the_request_is_accepted() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}

	@Then("^The schedule is stored in the DB$")
	public void the_schedule_is_stored_in_the_DB() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}

}
