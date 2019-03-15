package org.unifi.ft.rehearsal.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.exceptions.ScheduleNotFoundException;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;
import org.unifi.ft.rehearsal.services.BandService;
import org.unifi.ft.rehearsal.services.Scheduler;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SchedulePageWebController.class)
public class SchedulePageWebControllerTest {

	private MockMvc mvc;
	private MultiValueMap<String, String> params;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private BandService bandService;

	@MockBean
	private Scheduler scheduler;

	@MockBean
	private IScheduleMongoRepository schedulesRepository;

	@MockBean
	private IBandDetailsMongoRepository bandRepository;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		params = new HttpHeaders();
	}

	@Test
	@WithMockUser("username")
	public void testGetIndex() throws Exception {
		mvc.perform(get(SchedulePageWebController.SCHEDULE_URI).sessionAttr("user", "username"))
				.andExpect(view().name("schedule")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser("username")
	public void testClearSession() throws Exception {
		mvc.perform(post(SchedulePageWebController.CLEAR_SESSION_URI).sessionAttr("user", "username").with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
	}

	@Test
	@WithMockUser("username")
	public void testRehearsal() throws Exception {
		params.add("year", "2121");
		params.add("month", "12");
		params.add("day", "12");
		params.add("hour", "12");
		params.add("minutes", "12");
		params.add("room", RehearsalRoom.FIRSTROOM.name());

		DateTime toCheck = new DateTime(2121, 12, 12, 12, 12, 0);

		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().isOk());

		verify(scheduler, times(1)).initAndSaveSchedule("username", toCheck, RehearsalRoom.FIRSTROOM);
	}

	@Test
	@WithMockUser("username")
	public void testWrongNumberFormatRehearsal() throws Exception {
		params.add("year", "aaa");
		params.add("month", "12");
		params.add("day", "12");
		params.add("hour", "12");
		params.add("minutes", "12");
		params.add("room", RehearsalRoom.FIRSTROOM.name());

		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.NUMBER_ERROR_MESSAGE));

		verify(scheduler, times(0)).initAndSaveSchedule(any(String.class), any(DateTime.class),
				any(RehearsalRoom.class));
	}

	@Test
	@WithMockUser("username")
	public void testRoomNotFreeRehearsal() throws Exception {
		params.add("year", "2121");
		params.add("month", "12");
		params.add("day", "12");
		params.add("hour", "12");
		params.add("minutes", "12");
		params.add("room", RehearsalRoom.FIRSTROOM.name());

		DateTime toCheck = new DateTime(2121, 12, 12, 12, 12, 0);

		given(scheduler.initAndSaveSchedule(eq("username"), eq(toCheck), eq(RehearsalRoom.FIRSTROOM)))
				.willThrow(RoomNotFreeException.class);

		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.ROOM_ERROR_MESSAGE));

		verify(scheduler, times(1)).initAndSaveSchedule(eq("username"), eq(toCheck), eq(RehearsalRoom.FIRSTROOM));
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

		DateTime toCheck = new DateTime(2001, 12, 12, 12, 12, 0);

		given(scheduler.initAndSaveSchedule(eq("username"), eq(toCheck), eq(RehearsalRoom.FIRSTROOM)))
				.willThrow(InvalidTimeException.class);

		mvc.perform(post(SchedulePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username")
				.with(csrf())).andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.TIME_ERROR_MESSAGE));

		verify(scheduler, times(1)).initAndSaveSchedule(eq("username"), eq(toCheck), eq(RehearsalRoom.FIRSTROOM));
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByName() throws Exception {
		Schedule temp = new Schedule("username", new DateTime(),
				new DateTime().plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION),
				RehearsalRoom.FIRSTROOM);
		
		List<Schedule> schedules = new ArrayList<>();
		schedules.add(temp);
		
		given(scheduler.findSchedulesByBand("username")).willReturn(schedules);
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_NAME_URI).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("schedules", schedules));
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByNameWhenItIsEmpty() throws Exception {
		given(scheduler.findSchedulesByBand("username")).willReturn(new ArrayList<>());
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_NAME_URI).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("schedules", SchedulePageWebController.NO_SCHEDULES_MESSAGE));
	}
	
	@Test
	@WithMockUser("username")
	public void testGetSchedulesByNameWhenThereIsNoName() throws Exception {
	given(scheduler.findSchedulesByBand("username")).willReturn(new ArrayList<>());
		
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

		params.add("year", String.valueOf((startDate.getYear())));
		params.add("month", String.valueOf((startDate.getMonthOfYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		given(scheduler.findSchedulesByDate(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth())).willReturn(schedules);
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("schedules", schedules));
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenItIsEmpty() throws Exception {
		given(scheduler.findSchedulesByDate(any(Integer.class), any(Integer.class), any(Integer.class))).willReturn(new ArrayList<>());

		DateTime startDate = new DateTime();
		
		params.add("year", String.valueOf((startDate.getYear())));
		params.add("month", String.valueOf((startDate.getMonthOfYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("schedules", SchedulePageWebController.NO_SCHEDULES_MESSAGE));
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenNoYearAttribute() throws Exception {
		given(scheduler.findSchedulesByDate(any(Integer.class), any(Integer.class), any(Integer.class))).willReturn(new ArrayList<>());

		DateTime startDate = new DateTime();
		
		params.add("month", String.valueOf((startDate.getMonthOfYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenNoMonthAttribute() throws Exception {
		given(scheduler.findSchedulesByDate(any(Integer.class), any(Integer.class), any(Integer.class))).willReturn(new ArrayList<>());

		DateTime startDate = new DateTime();
		
		params.add("year", String.valueOf((startDate.getYear())));
		params.add("day", String.valueOf((startDate.getDayOfMonth())));
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_DATE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByDateWhenNoDayAttribute() throws Exception {
		given(scheduler.findSchedulesByDate(any(Integer.class), any(Integer.class), any(Integer.class))).willReturn(new ArrayList<>());

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

		params.add("room", RehearsalRoom.FIRSTROOM.name());
		
		given(scheduler.findSchedulesByRoom(RehearsalRoom.FIRSTROOM)).willReturn(schedules);
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_ROOM_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("schedules", schedules));
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByRoomWhenItIsEmpty() throws Exception {
		given(scheduler.findSchedulesByRoom(any(RehearsalRoom.class))).willReturn(new ArrayList<>());

		params.add("room", RehearsalRoom.FIRSTROOM.name());
		
		mvc.perform(get(SchedulePageWebController.FIND_BY_ROOM_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("schedules", SchedulePageWebController.NO_SCHEDULES_MESSAGE));
	}

	@Test
	@WithMockUser("username")
	public void testGetSchedulesByRoomWhenNoRoomAttribute() throws Exception {
		given(scheduler.findSchedulesByRoom(any(RehearsalRoom.class))).willReturn(new ArrayList<>());

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
		
		params.add("year", String.valueOf(startDate.getYear()));
		params.add("month", String.valueOf(startDate.getMonthOfYear()));
		params.add("day", String.valueOf(startDate.getDayOfMonth()));
		params.add("hour", String.valueOf(startDate.getHourOfDay()));
		params.add("minutes", String.valueOf(startDate.getMinuteOfHour()));
		params.add("room", RehearsalRoom.FIRSTROOM.name());
		
		given(scheduler.deleteSchedule("username", startDate, RehearsalRoom.FIRSTROOM)).willReturn(temp);
		
		mvc.perform(get(SchedulePageWebController.DELETE_SCHEDULE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().isOk())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.SCHEDULE_REMOVED_MESSAGE));
	}
	
	@Test
	@WithMockUser("username")
	public void testDeleteScheduleFailure() throws Exception {
		DateTime startDate = new DateTime();
		
		params.add("year", String.valueOf(startDate.getYear()));
		params.add("month", String.valueOf(startDate.getMonthOfYear()));
		params.add("day", String.valueOf(startDate.getDayOfMonth()));
		params.add("hour", String.valueOf(startDate.getHourOfDay()));
		params.add("minutes", String.valueOf(startDate.getMinuteOfHour()));
		params.add("room", RehearsalRoom.FIRSTROOM.name());
		
		given(scheduler.deleteSchedule(eq("username"), any(DateTime.class), eq(RehearsalRoom.FIRSTROOM))).willThrow(ScheduleNotFoundException.class);
		
		mvc.perform(get(SchedulePageWebController.DELETE_SCHEDULE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute(SchedulePageWebController.INFO, SchedulePageWebController.NO_SCHEDULES_MESSAGE));
	}
	
	@Test
	@WithMockUser("username")
	public void testDeleteScheduleMissingAttribute() throws Exception {
		DateTime startDate = new DateTime();
		
		params.add("month", String.valueOf(startDate.getMonthOfYear()));
		params.add("day", String.valueOf(startDate.getDayOfMonth()));
		params.add("hour", String.valueOf(startDate.getHourOfDay()));
		params.add("minutes", String.valueOf(startDate.getMinuteOfHour()));
		params.add("room", RehearsalRoom.FIRSTROOM.name());
		
		given(scheduler.deleteSchedule(eq("username"), any(DateTime.class), eq(RehearsalRoom.FIRSTROOM))).willThrow(ScheduleNotFoundException.class);
		
		mvc.perform(get(SchedulePageWebController.DELETE_SCHEDULE_URI).params(params).sessionAttr("user", "username"))
				.andExpect(status().is4xxClientError());
	}

}
