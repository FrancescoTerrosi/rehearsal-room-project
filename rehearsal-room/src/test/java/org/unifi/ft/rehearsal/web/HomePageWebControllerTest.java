package org.unifi.ft.rehearsal.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
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
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;
import org.unifi.ft.rehearsal.services.BandService;
import org.unifi.ft.rehearsal.services.Scheduler;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HomePageWebController.class)
public class HomePageWebControllerTest {

	private MockMvc mvc;
	private MultiValueMap<String, String> params = new HttpHeaders();

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
	}

	@Test
	@WithMockUser("username")
	public void testGetIndex() throws Exception {
		mvc.perform(get(HomePageWebController.HOME_URI).sessionAttr("user", "username")).andExpect(view().name("home"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser("username")
	public void testClearSession() throws Exception {
		mvc.perform(post(HomePageWebController.CLEAR_SESSION_URI).sessionAttr("user", "username").with(csrf()))
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

		DateTime toCheck = new DateTime(2121,12,12,12,12,0);
		
		mvc.perform(post(HomePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username").with(csrf()))
				.andExpect(status().isOk());
		
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
		
		mvc.perform(post(HomePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username").with(csrf()))
			.andExpect(status().is4xxClientError())
			.andExpect(model().attribute("error", HomePageWebController.NUMBER_ERROR_MESSAGE));
		
		verify(scheduler, times(0)).initAndSaveSchedule(any(String.class), any(DateTime.class), any(RehearsalRoom.class));
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
		
		DateTime toCheck = new DateTime(2121,12,12,12,12,0);
		
		given(scheduler.initAndSaveSchedule(
				eq("username"), eq(toCheck), eq(RehearsalRoom.FIRSTROOM)))
		.willThrow(RoomNotFreeException.class);
		
		mvc.perform(post(HomePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username").with(csrf()))
			.andExpect(status().is4xxClientError())
			.andExpect(model().attribute("error", HomePageWebController.ROOM_ERROR_MESSAGE));
		
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
		
		DateTime toCheck = new DateTime(2001,12,12,12,12,0);
		
		given(scheduler.initAndSaveSchedule(
				eq("username"), eq(toCheck), eq(RehearsalRoom.FIRSTROOM)))
		.willThrow(InvalidTimeException.class);
		
		mvc.perform(post(HomePageWebController.SCHEDULE_URI).params(params).sessionAttr("user", "username").with(csrf()))
			.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", HomePageWebController.TIME_ERROR_MESSAGE));

		verify(scheduler, times(1)).initAndSaveSchedule(eq("username"), eq(toCheck), eq(RehearsalRoom.FIRSTROOM));
	}

}
