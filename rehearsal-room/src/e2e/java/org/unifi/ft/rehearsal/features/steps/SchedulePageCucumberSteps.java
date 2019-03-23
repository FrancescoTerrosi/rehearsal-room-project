package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
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
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;
import org.unifi.ft.rehearsal.services.Scheduler;
import org.unifi.ft.rehearsal.web.SchedulePageWebController;

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
	private IScheduleMongoRepository scheduleRepo;

	@Autowired
	private BCryptPasswordEncoder encoder;

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

	@Given("^The user is registered into the system$")
	public void theUserIsRegisteredIntoTheSystem() throws Throwable {
		userRepo.save(createTestUser());
	}

	@Given("^The user is logged into the system$")
	public void theUserIsLoggedIntoTheSystem() throws Throwable {
		driver.get(HOMEPAGE + port + "/login");
		driver.findElement(By.id("username")).sendKeys("BandName");
		driver.findElement(By.id("password")).sendKeys("BandPw");
		driver.findElement(By.id("submit")).click();
	}

	@Given("^There are some schedules in the DB$")
	public void thereAreSomeSchedulesInTheDB() throws Throwable {
		DateTime startDate = new DateTime(2121, 12, 12, 12, 12, 0);
		DateTime endDate = startDate.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		Schedule s = new Schedule("OneBand", startDate, endDate, RehearsalRoom.FIRSTROOM);
		scheduleRepo.save(s);
		assertEquals(1, scheduleRepo.count());
	}

	@When("^The user requests the /schedule url$")
	public void the_user_requests_the_schedule_url() throws Throwable {
		driver.get(HOMEPAGE + port + "/schedule");
		assertEquals(HOMEPAGE + port + "/schedule", driver.getCurrentUrl());
	}

	@When("^The user schedule for a free room in valid date$")
	public void the_user_schedule_for_a_free_room_in_valid_date() throws Throwable {
		WebElement scheduleDiv = driver.findElement(By.id("scheduleContent"));
		submitSchedule(scheduleDiv, "2121", "12", "12", "12", "12", "FIRSTROOM");
	}

	@When("^The user schedule for a free room in a day the does not exist$")
	public void the_user_schedule_for_a_free_room_in_a_day_the_does_not_exist() throws Throwable {
		WebElement scheduleDiv = driver.findElement(By.id("scheduleContent"));
		submitSchedule(scheduleDiv, "2121", "2", "30", "12", "12", "FIRSTROOM");
	}

	@When("^The user schedule for a free room but leaving a blank field$")
	public void the_user_schedule_for_a_free_room_but_leaving_a_blank_field() throws Throwable {
		WebElement scheduleDiv = driver.findElement(By.id("scheduleContent"));
		submitSchedule(scheduleDiv, "2121", "", "30", "12", "12", "FIRSTROOM");
	}

	@When("^The user schedule for a room that is not free$")
	public void the_user_schedule_for_a_room_that_is_not_free() throws Throwable {
		WebElement scheduleDiv = driver.findElement(By.id("scheduleContent"));
		submitSchedule(scheduleDiv, "2121", "12", "12", "12", "12", "FIRSTROOM");
	}

	@When("^The user schedule for a room in a time t < \\(now \\+ (\\d+) minutes\\)$")
	public void the_user_schedule_for_a_room_in_a_time_t_now_minutes(int arg1) throws Throwable {
		WebElement scheduleDiv = driver.findElement(By.id("scheduleContent"));
		submitSchedule(scheduleDiv, "2015", "12", "12", "12", "12", "FIRSTROOM");
	}

	@Then("^The request is accepted$")
	public void the_request_is_accepted() throws Throwable {
		assertEquals(SchedulePageWebController.SCHEDULE_SAVED_MESSAGE, driver.findElement(By.id("infos")).getText());
	}

	@Then("^The schedule is stored in the DB$")
	public void the_schedule_is_stored_in_the_DB() throws Throwable {
		assertEquals(1, scheduleRepo.count());
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 0);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		Schedule s = scheduleRepo.findAll().get(0);
		assertEquals(s.getBand(), "BandName");
		assertEquals(s.getRoom(), RehearsalRoom.FIRSTROOM);
		assertEquals(s.getStartDate(), start);
		assertEquals(s.getEndDate(), end);
	}

	@Then("^An invalid date message is shown$")
	public void an_invalid_date_message_is_shown() throws Throwable {
		assertEquals(SchedulePageWebController.NUMBER_ERROR_MESSAGE, driver.findElement(By.id("infos")).getText());
	}

	@Then("^A room not free message is shown$")
	public void a_room_not_free_message_is_shown() throws Throwable {
		assertEquals(SchedulePageWebController.ROOM_ERROR_MESSAGE, driver.findElement(By.id("infos")).getText());
	}

	@Then("^The schedule is not stored in the empty DB$")
	public void theScheduleIsNotStoredInTheEmptyDB() throws Throwable {
		assertEquals(0, scheduleRepo.count());
	}

	@Then("^The schedule is not stored in the DB$")
	public void the_schedule_is_not_stored_in_the_DB() throws Throwable {
		assertEquals(1, scheduleRepo.count());
	}

	@Then("^A funny message is shown$")
	public void a_funny_message_is_shown() throws Throwable {
		assertEquals(SchedulePageWebController.TIME_ERROR_MESSAGE, driver.findElement(By.id("infos")).getText());
	}

	private void submitSchedule(WebElement div, String year, String month, String day, String hour, String minutes,
			String room) {
		div.findElement(By.id("year")).sendKeys(year);
		div.findElement(By.id("month")).sendKeys(month);
		div.findElement(By.id("day")).sendKeys(day);
		div.findElement(By.id("hour")).sendKeys(hour);
		div.findElement(By.id("minutes")).sendKeys(minutes);
		div.findElement(By.id("room")).sendKeys(room);
		div.findElement(By.id("submit")).click();
	}

	private BandDetails createTestUser() {
		BandDetails toSave = new BandDetails("BandName", encoder.encode("BandPw"), "USER");
		return toSave;
	}

}
