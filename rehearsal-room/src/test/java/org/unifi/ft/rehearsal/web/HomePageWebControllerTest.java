package org.unifi.ft.rehearsal.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;
import org.unifi.ft.rehearsal.services.BandService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HomePageWebController.class)
public class HomePageWebControllerTest {

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private BandService bandService;

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
		mvc.perform(get("/home").sessionAttr("user", "username")).andExpect(view().name("home"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser("username")
	public void testClearSession() throws Exception {
		mvc.perform(post("/clear_session").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

}
