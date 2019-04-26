package org.unifi.ft.rehearsal.web;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
public class RegisterPageWebControllerIT extends AbstractLoginRegisterUtilForIT {

	private MultiValueMap<String, String> params = new HttpHeaders();
	
	@Before
	public void setup() {
		super.setup();
		params.clear();
	}
	
	@After
	public void clearAll() {
		super.clearAll();
		params.clear();
	}
	
	@Test
	public void testGetRegisterIndex() throws Exception {
		getMvc().perform(get(RegisterPageWebController.REGISTER_URI))
			.andExpect(view().name("registerPage"))
			.andExpect(status().isOk());
	}

	@Test
	public void testRegister() throws Exception {
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "userPassword");
		
		getMvc().perform(post(RegisterPageWebController.REGISTER_URI).params(params).with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/"));
		
		assertEquals(1, getRepository().count());
	}

	@Test
	public void testRegisterUserAlreadyExists() throws Exception {
		getRepository().save(createUser("userName","userPw"));
		assertEquals(1, getRepository().count());
		
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "userPassword");
		
		getMvc().perform(post(RegisterPageWebController.REGISTER_URI).params(params).with(csrf()))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.REGISTRATION_USERNAME_ERROR));

		assertEquals(1, getRepository().count());
	}
	
	@Test
	public void testRegisterWrongPassword() throws Exception {		
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "wrongPassword");
		
		getMvc().perform(post(RegisterPageWebController.REGISTER_URI).params(params).with(csrf()))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.REGISTRATION_PASSW_ERROR));

		assertEquals(0, getRepository().count());
	}
		
}
