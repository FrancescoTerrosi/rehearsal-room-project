package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
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

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
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
	
	@When("^The user schedule for a free room but using illegal charachters$")
	public void theUserScheduleForAFreeRoomButUsingIllegalCharachters() throws Throwable {
		WebElement scheduleDiv = driver.findElement(By.id("scheduleContent"));
		submitSchedule(scheduleDiv, "aaaa", "12", "12", "12", "12", "FIRSTROOM");
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

	@When("^The user requests the list of saved schedules by date$")
	public void the_user_requests_the_list_of_saved_schedules_by_date() throws Throwable {
		WebElement byDateDiv = driver.findElement(By.id("byDateDiv"));
		searchScheduleByDate(byDateDiv, "2121", "12", "12");
	}

	@When("^There are schedules saved on that day$")
	public void there_are_schedules_saved_on_that_day() throws Throwable {
		assertEquals(1, scheduleRepo.count());
		Schedule s = scheduleRepo.findAll().get(0);
		assertEquals(s.getStartDate().getYear(), 2121);
		assertEquals(s.getStartDate().getMonthOfYear(), 12);
		assertEquals(s.getStartDate().getDayOfMonth(), 12);
	}

	@Then("^A list of saved schedules is shown$")
	public void a_list_of_saved_schedules_is_shown() throws Throwable {
		assertEquals("OneBand scheduled for room: FIRSTROOM on date: 2121/12/12 - 12:12",
				driver.findElement(By.id("schedules")).getText());
	}

	@When("^The user requests the list of saved schedules by date on a free day$")
	public void the_user_requests_the_list_of_saved_schedules_by_date_on_a_free_day() throws Throwable {
		WebElement byDateDiv = driver.findElement(By.id("byDateDiv"));
		searchScheduleByDate(byDateDiv, "2120", "12", "12");
	}

	@When("^There are not schedules saved on that day$")
	public void there_are_not_schedules_saved_on_that_day() throws Throwable {
		assertEquals(1, scheduleRepo.count());
		Schedule s = scheduleRepo.findAll().get(0);
		assertFalse(s.getStartDate().getYear() == 2120);
	}

	@Then("^A proper message is shown to the user$")
	public void a_proper_message_is_shown_to_the_user() throws Throwable {
		assertEquals("No schedules found!", driver.findElement(By.id("schedules")).getText());
	}

	@Then("^The user requests the list of saved schedules by date but leaves a blank field$")
	public void theUserRequestsTheListOfSavedSchedulesByDateButLeavesABlankField() throws Throwable {
		WebElement byDateDiv = driver.findElement(By.id("byDateDiv"));
		searchScheduleByDate(byDateDiv, "", "12", "12");
	}

	@Then("^The user requests the list of saved schedules by date but using illegal charachters$")
	public void theUserRequestsTheListOfSavedSchedulesByDateButUsingIllegalCharachters() throws Throwable {
		WebElement byDateDiv = driver.findElement(By.id("byDateDiv"));
		searchScheduleByDate(byDateDiv, "aaaa", "12", "12");
	}

	@Given("^The user scheduled for rehearsals$")
	public void the_user_scheduled_for_rehearsals() throws Throwable {
		WebElement scheduleDiv = driver.findElement(By.id("scheduleContent"));
		submitSchedule(scheduleDiv, "2121", "12", "12", "12", "12", "FIRSTROOM");
	}

	@When("^The user requests the list of saved schedules by name$")
	public void the_user_requests_the_list_of_saved_schedules_by_name() throws Throwable {
		driver.findElement(By.id("byNameDiv")).findElement(By.name("submit")).click();
	}

	@Then("^His list of saved schedules is shown$")
	public void his_list_of_saved_schedules_is_shown() throws Throwable {
		assertEquals("BandName scheduled for room: FIRSTROOM on date: 2121/12/12 - 12:12 Delete",
				driver.findElement(By.id("yourSchedules")).getText());
	}

	@When("^The user click the delete button$")
	public void the_user_click_the_delete_button() throws Throwable {
		driver.findElement(By.id("yourSchedules")).findElement(By.linkText("Delete")).click();
	}

	@Then("^The schedule is removed from the db$")
	public void the_schedule_is_removed_from_the_db() throws Throwable {
		assertEquals(0, scheduleRepo.count());
	}

	@When("^The user requests the list of saved schedules by room$")
	public void the_user_requests_the_list_of_saved_schedules_by_room() throws Throwable {
		WebElement roomDiv = driver.findElement(By.id("byRoomDiv"));
		roomDiv.findElement(By.id("room")).sendKeys("FIRSTROOM");
		roomDiv.findElement(By.name("submit")).click();
	}

	@When("^The user requests the list of saved schedules by room for a free room$")
	public void the_user_requests_the_list_of_saved_schedules_by_room_for_a_free_room() throws Throwable {
		WebElement roomDiv = driver.findElement(By.id("byRoomDiv"));
		roomDiv.findElement(By.id("room")).sendKeys("SECONDROOM");
		roomDiv.findElement(By.name("submit")).click();
	}

	@When("^The user clicks on the Logout button$")
	public void the_user_clicks_on_the_Logout_button() throws Throwable {
		driver.findElement(By.id("logoutForm")).submit();
	}

	@Then("^The user is redirected to the homepage$")
	public void the_user_is_redirected_to_the_homepage() throws Throwable {
		assertEquals(HOMEPAGE+port+"/", driver.getCurrentUrl());
	}

	@Then("^The user can not visit /schedule page$")
	public void the_user_can_not_visit_schedule_page() throws Throwable {
		driver.get(HOMEPAGE+port+"/schedule");
		assertEquals(HOMEPAGE+port+"/login", driver.getCurrentUrl());
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

	private void searchScheduleByDate(WebElement byDateDiv, String year, String month, String day) {
		byDateDiv.findElement(By.id("year")).sendKeys(year);
		byDateDiv.findElement(By.id("month")).sendKeys(month);
		byDateDiv.findElement(By.id("day")).sendKeys(day);
		byDateDiv.findElement(By.name("submit")).click(); // Questo By.name inspiegabile eh
	}

	private BandDetails createTestUser() {
		BandDetails toSave = new BandDetails("BandName", encoder.encode("BandPw"), "USER");
		return toSave;
	}

}
