package org.unifi.ft.rehearsal.web;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.unifi.ft.rehearsal.configurations.MongoConfig;
import org.unifi.ft.rehearsal.configurations.WebSecurityConfig;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;
import org.unifi.ft.rehearsal.services.Scheduler;

@RunWith(SpringRunner.class)
@Import({WebSecurityConfig.class, MongoConfig.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SchedulePageWebControllerIT {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IScheduleMongoRepository repository;

	private MockMvc mvc;
	
	private MultiValueMap<String, String> params = new HttpHeaders();
	
	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		repository.deleteAll();
		params = new HttpHeaders();
	}
	
	@After
	public void clearAll() {
		repository.deleteAll();
		params.clear();
	}
	
	@Test
	@WithMockUser("username")
	public void testGetSchedulePage() throws Exception {
		mvc.perform(get(SchedulePageWebController.SCHEDULE_URI).sessionAttr("user", "username"))
			.andExpect(status().isOk())
			.andExpect(view().name(SchedulePageWebController.SCHEDULE_PAGE));
	}
	
	@Test
	@WithMockUser("username")
	public void testClearSession() throws Exception {
		HttpSession session = mvc.perform(post(SchedulePageWebController.CLEAR_SESSION_URI).sessionAttr("user", "username").with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/logout")).andReturn().getRequest().getSession();
	
		assertNull(session.getAttribute("user"));
	}
	
	@Test
	@WithMockUser("username")
	public void testScheduleRehearsal() throws Exception {
		params.add("year", "2121");
		params.add("month", "12");
		params.add("day", "12");
		params.add("hour", "12");
		params.add("minutes", "12");
		params.add("room", RehearsalRoom.FIRSTROOM.name());

		DateTime startDate = new DateTime(2121, 12, 12, 12, 12, 0);
		DateTime endDate = startDate.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().isOk());
		
		assertEquals(1, repository.count());
		
		List<Schedule> schedules =  repository.findAll();
		Schedule toCheck = schedules.get(0);
		
		assertEquals("username", toCheck.getBand());
		assertEquals(startDate, toCheck.getStartDate());
		assertEquals(endDate, toCheck.getEndDate());
		assertEquals(RehearsalRoom.FIRSTROOM, toCheck.getRoom());
	}
	
	@Test
	@WithMockUser("username")
	public void testWrongNumberFormatScheduleRehearsal() throws Exception {
		params.add("year", "aaa");
		params.add("month", "12");
		params.add("day", "12");
		params.add("hour", "12");
		params.add("minutes", "12");
		params.add("room", RehearsalRoom.FIRSTROOM.name());

		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.NUMBER_ERROR_MESSAGE));

		assertEquals(0, repository.count());
	}
	

	@Test
	@WithMockUser("username")
	public void testRoomNotFreeRehearsal() throws Exception {
		DateTime startDate = new DateTime(2121, 12, 12, 12, 12, 0);
		DateTime endDate = startDate.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		
		Schedule s = new Schedule("anotherBand", startDate, endDate, RehearsalRoom.FIRSTROOM);
		
		repository.save(s);
		assertEquals(1, repository.count());
		
		params.add("year", "2121");
		params.add("month", "12");
		params.add("day", "12");
		params.add("hour", "12");
		params.add("minutes", "12");
		params.add("room", RehearsalRoom.FIRSTROOM.name());


		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.ROOM_ERROR_MESSAGE));

		assertEquals(1, repository.count());
	}

	@Test
	@WithMockUser("username")
	public void testWrongTimeRehearsal() throws Exception {
		params.add("year", "2001");
		params.add("month", "12");
		params.add("day", "12");
		params.add("hour", "12");
		params.add("minutes", "12");
		params.add("room", RehearsalRoom.FIRSTROOM.name());

		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.TIME_ERROR_MESSAGE));

		assertEquals(0, repository.count());

	}
	

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByName() throws Exception {
		DateTime t = new DateTime();
		Schedule temp = new Schedule("username", t,
				t.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION),
				RehearsalRoom.FIRSTROOM);
		
		List<Schedule> schedules = new ArrayList<>();
		schedules.add(temp);
		
		repository.save(temp);
		assertEquals(1, repository.count());
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_NAME_URI).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute(SchedulePageWebController.YOUR_SCHEDULE_DIV, schedules));
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByNameWhenItIsEmpty() throws Exception {
		assertEquals(0, repository.count());
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_NAME_URI).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("schedules", SchedulePageWebController.NO_SCHEDULES_MESSAGE));
	}
	
	@Test
	@WithMockUser("username")
	public void testGetSchedulesByNameWhenThereIsNoName() throws Exception {
		mvc.perform(get(SchedulePageWebController.FIND_BY_NAME_URI))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDate() throws Exception {
		DateTime startDate = new DateTime();
		Schedule temp = new Schedule("username", startDate,
				startDate.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION),
				RehearsalRoom.FIRSTROOM);
		
		List<Schedule> schedules = new ArrayList<>();
		schedules.add(temp);
		
		repository.save(temp);
		assertEquals(1, repository.count());

		params.add("year", String.valueOf((startDate.getYear())));
		params.add("month", String.valueOf((startDate.getMonthOfYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute(SchedulePageWebController.QUERIES, schedules));
	}
	
	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenItIsEmpty() throws Exception {
		assertEquals(0, repository.count());
		DateTime startDate = new DateTime();
		
		params.add("year", String.valueOf((startDate.getYear())));
		params.add("month", String.valueOf((startDate.getMonthOfYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute(SchedulePageWebController.QUERIES, SchedulePageWebController.NO_SCHEDULES_MESSAGE));
	}
	
	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenNoYearAttribute() throws Exception {
		DateTime startDate = new DateTime();
		
		params.add("month", String.valueOf((startDate.getMonthOfYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenNoMonthAttribute() throws Exception {
		DateTime startDate = new DateTime();
		
		params.add("year", String.valueOf((startDate.getYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}
	

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenNoDayAttribute() throws Exception {
		DateTime startDate = new DateTime();

		params.add("year", String.valueOf((startDate.getYear())));
		params.add("month", String.valueOf((startDate.getMonthOfYear())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByRoom() throws Exception {
		DateTime startDate = new DateTime();
		Schedule temp = new Schedule("username", startDate,
				startDate.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION),
				RehearsalRoom.FIRSTROOM);
		
		List<Schedule> schedules = new ArrayList<>();
		schedules.add(temp);

		repository.save(temp);
		assertEquals(1, repository.count());
		
		params.add("room", RehearsalRoom.FIRSTROOM.name());
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_ROOM_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute(SchedulePageWebController.QUERIES, schedules));
	}
	
	@Test
	@WithMockUser("username")
	public void testGetSchedulesByRoomWhenItIsEmpty() throws Exception {
		assertEquals(0, repository.count());
		params.add("room", RehearsalRoom.FIRSTROOM.name());
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_ROOM_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute(SchedulePageWebController.QUERIES, SchedulePageWebController.NO_SCHEDULES_MESSAGE));
		}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByRoomWhenNoRoomAttribute() throws Exception {
		mvc.perform(get(SchedulePageWebController.FIND_BY_ROOM_URI).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	@WithMockUser("username")
	public void testDeleteScheduleSuccess() throws Exception {
		DateTime startDate = new DateTime();
		Schedule temp = new Schedule("username", startDate,
				startDate.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION),
				RehearsalRoom.FIRSTROOM);
		BigInteger id = new BigInteger("0");
		temp.setId(id);
		
		repository.save(temp);
		assertEquals(1, repository.count());
		
		params.add("id", "0");

		mvc.perform(get(SchedulePageWebController.DELETE_SCHEDULE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.SCHEDULE_REMOVED_MESSAGE));
	
		assertEquals(0, repository.count());
	}
	
	@Test
	@WithMockUser("username")
	public void testDeleteScheduleFailure() throws Exception {
		params.add("id", "1");
		
		mvc.perform(get(SchedulePageWebController.DELETE_SCHEDULE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.NO_SCHEDULES_MESSAGE));
	}
	
	@Test
	@WithMockUser("username")
	public void testDeleteScheduleMissingAttribute() throws Exception {

		mvc.perform(get(SchedulePageWebController.DELETE_SCHEDULE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}

}
